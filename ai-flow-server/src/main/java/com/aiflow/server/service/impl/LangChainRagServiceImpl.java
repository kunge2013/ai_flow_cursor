package com.aiflow.server.service.impl;

import com.aiflow.server.dto.VectorSearchDtos.VectorSearchRequest;
import com.aiflow.server.dto.VectorSearchDtos.VectorSearchResponse;
import com.aiflow.server.dto.VectorSearchDtos.VectorSearchResult;
import com.aiflow.server.entity.KnowledgeBase;
import com.aiflow.server.entity.VectorDocument;
import com.aiflow.server.mapper.KnowledgeBaseMapper;
import com.aiflow.server.mapper.VectorDocumentMapper;
import com.aiflow.server.service.LangChainRagService;
import com.aiflow.server.service.IdService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LangChain4j RAG服务实现
 * 集成文档解析、文本分割、向量化和检索功能
 */
@Slf4j
@Service
public class LangChainRagServiceImpl implements LangChainRagService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final VectorDocumentMapper vectorDocumentMapper;
    private final ExecutorService executorService;
    private final Tika tika;
    
    @Value("${ai.model.openai.api-key:}")
    private String openaiApiKey;
    
    @Value("${ai.model.openai.embedding-model:text-embedding-ada-002}")
    private String openaiEmbeddingModel;
    
    @Value("${milvus.host:localhost}")
    private String milvusHost;
    
    @Value("${milvus.port:19530}")
    private int milvusPort;
    
    @Value("${milvus.collection:ai_flow_documents}")
    private String milvusCollection;

    public LangChainRagServiceImpl(KnowledgeBaseMapper knowledgeBaseMapper, 
                                   VectorDocumentMapper vectorDocumentMapper) {
        this.knowledgeBaseMapper = knowledgeBaseMapper;
        this.vectorDocumentMapper = vectorDocumentMapper;
        this.executorService = Executors.newFixedThreadPool(4);
        this.tika = new Tika();
    }

    @Override
    public DocumentProcessResult processDocument(String kbId, MultipartFile file) {
        long startTime = System.currentTimeMillis();
        DocumentProcessResult result = new DocumentProcessResult();
        
        try {
            log.info("开始处理文档: {}, 知识库: {}", file.getOriginalFilename(), kbId);
            
            // 1. 解析文档内容
            String content = parseDocumentContent(file);
            
            // 2. 处理文档内容
            result = processDocumentContent(kbId, file.getOriginalFilename(), content, 
                    getFileType(file.getOriginalFilename()));
            
            // 3. 异步进行RAG处理
            CompletableFuture.runAsync(() -> {
                try {
                    performRagProcessing(kbId, file.getOriginalFilename(), content);
                } catch (Exception e) {
                    log.error("RAG处理失败: {}", file.getOriginalFilename(), e);
                }
            }, executorService);
            
            result.setProcessingTime(System.currentTimeMillis() - startTime);
            result.setStatus("success");
            result.setMessage("文档处理成功，RAG处理已启动");
            
        } catch (Exception e) {
            log.error("文档处理失败: {}", file.getOriginalFilename(), e);
            result.setStatus("failed");
            result.setMessage("文档处理失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public DocumentProcessResult processDocumentContent(String kbId, String title, String content, String contentType) {
        long startTime = System.currentTimeMillis();
        DocumentProcessResult result = new DocumentProcessResult();
        
        try {
            log.info("开始处理文档内容: {}, 知识库: {}", title, kbId);
            
            // 1. 保存文档到数据库
            VectorDocument doc = saveDocumentToDatabase(kbId, title, content, contentType);
            result.setDocumentId(doc.getId());
            
            // 2. 文本分割
            List<String> chunks = splitTextIntoChunks(content);
            result.setChunks(chunks);
            result.setTotalChunks(chunks.size());
            
            // 3. 异步进行RAG处理
            CompletableFuture.runAsync(() -> {
                try {
                    performRagProcessing(kbId, title, content);
                } catch (Exception e) {
                    log.error("RAG处理失败: {}", title, e);
                }
            }, executorService);
            
            result.setProcessingTime(System.currentTimeMillis() - startTime);
            result.setStatus("success");
            result.setMessage("文档内容处理成功，RAG处理已启动");
            
        } catch (Exception e) {
            log.error("文档内容处理失败: {}", title, e);
            result.setStatus("failed");
            result.setMessage("文档内容处理失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public VectorSearchResponse searchSimilar(VectorSearchRequest request) {
        try {
            log.info("开始RAG相似度搜索: {}, 知识库: {}", request.getQuery(), request.getKbId());
            
            // 1. 创建向量存储
            EmbeddingStore<TextSegment> embeddingStore = createEmbeddingStore(request.getKbId());
            
            // 2. 创建嵌入模型
            EmbeddingModel embeddingModel = createEmbeddingModel(request.getKbId());
            
            // 3. 执行搜索
            // 首先将查询文本转换为embedding
            Embedding queryEmbedding = embeddingModel.embed(request.getQuery()).content();
            List<EmbeddingMatch<TextSegment>> embeddingMatches = embeddingStore.findRelevant(
                    queryEmbedding,
                    request.getTopK(),
                    request.getScoreThreshold()
            );
            
            // 4. 构建响应
            VectorSearchResponse response = new VectorSearchResponse();
            response.setQuery(request.getQuery());
            response.setTotalCount((long) embeddingMatches.size());
            response.setSearchTime(System.currentTimeMillis());
            
            List<VectorSearchResult> results = new ArrayList<>();
            for (EmbeddingMatch<TextSegment> match : embeddingMatches) {
                TextSegment segment = match.embedded();
                VectorSearchResult result = new VectorSearchResult();
                result.setTitle(segment.metadata().get("title"));
                result.setContent(segment.text());
                // 从EmbeddingMatch中获取实际的相似度分数
                result.setScore(match.score());
                results.add(result);
            }
            response.setResults(results);
            
            log.info("RAG搜索完成，找到 {} 个结果", results.size());
            return response;
            
        } catch (Exception e) {
            log.error("RAG搜索失败", e);
            throw new RuntimeException("RAG搜索失败: " + e.getMessage());
        }
    }

    @Override
    public RagStatistics getRagStatistics(String kbId) {
        try {
            RagStatistics stats = new RagStatistics();
            stats.setKbId(kbId);
            
            // 获取知识库信息
            KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
            if (kb != null) {
                stats.setVectorModel(kb.getVectorModel());
            }
            
            // 统计文档数量
            long docCount = vectorDocumentMapper.countByKbId(kbId);
            stats.setTotalDocuments(docCount);
            
            // 统计处理完成的文档
            long completedDocs = vectorDocumentMapper.countByKbIdAndStatus(kbId, "completed");
            stats.setTotalEmbeddings(completedDocs);
            
            // 估算chunk数量（假设每个文档平均分成10个chunk）
            stats.setTotalChunks(completedDocs * 10);
            
            stats.setLastUpdated(System.currentTimeMillis());
            
            return stats;
            
        } catch (Exception e) {
            log.error("获取RAG统计信息失败: {}", kbId, e);
            throw new RuntimeException("获取RAG统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 解析文档内容
     */
    private String parseDocumentContent(MultipartFile file) throws IOException, TikaException {
        try {
            return tika.parseToString(file.getInputStream());
        } catch (Exception e) {
            log.warn("Tika解析失败，尝试直接读取文本: {}", file.getOriginalFilename());
            return new String(file.getBytes());
        }
    }

    /**
     * 文本分割
     */
    private List<String> splitTextIntoChunks(String content) {
        Document document = Document.from(content);
        DocumentSplitter splitter = DocumentSplitters.recursive(1000, 200);
        List<TextSegment> segments = splitter.split(document);
        
        List<String> chunks = new ArrayList<>();
        for (TextSegment segment : segments) {
            chunks.add(segment.text());
        }
        
        return chunks;
    }

    /**
     * 保存文档到数据库
     */
    private VectorDocument saveDocumentToDatabase(String kbId, String title, String content, String contentType) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
        String vectorModel = kb != null && kb.getVectorModel() != null ? kb.getVectorModel() : "openai";

        VectorDocument doc = new VectorDocument();
        doc.setId(IdService.newId());
        doc.setKbId(kbId);
        doc.setTitle(title);
        doc.setContent(content);
        doc.setContentType(contentType);
        doc.setFileSize((long) content.length());
        doc.setVectorModel(vectorModel);
        doc.setStatus("processing");
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(doc.getCreatedAt());
        
        vectorDocumentMapper.insert(doc);
        return doc;
    }

    /**
     * 执行RAG处理
     */
    private void performRagProcessing(String kbId, String title, String content) {
        try {
            log.info("开始RAG处理: {}", title);
            
            // 1. 创建向量存储
            EmbeddingStore<TextSegment> embeddingStore = createEmbeddingStore(kbId);
            
            // 2. 创建嵌入模型
            EmbeddingModel embeddingModel = createEmbeddingModel(kbId);
            
            // 3. 创建文档摄取器
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(DocumentSplitters.recursive(1000, 200))
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();
            
            // 4. 处理文档
            Document document = Document.from(content);
            document.metadata().add("title", title);
            document.metadata().add("kbId", kbId);
            document.metadata().add("timestamp", System.currentTimeMillis());
            
            ingestor.ingest(document);
            
            // 5. 更新数据库状态
            updateDocumentStatus(kbId, title, "completed");
            
            log.info("RAG处理完成: {}", title);
            
        } catch (Exception e) {
            log.error("RAG处理失败: {}", title, e);
            updateDocumentStatus(kbId, title, "failed");
        }
    }

    /**
     * 创建向量存储
     */
    private EmbeddingStore<TextSegment> createEmbeddingStore(String kbId) {
        String collectionName = milvusCollection + "_" + kbId;
        return MilvusEmbeddingStore.builder()
                .host(milvusHost)
                .port(milvusPort)
                .collectionName(collectionName)
                .dimension(1536) // OpenAI embedding维度
                .build();
    }

    /**
     * 创建嵌入模型
     */
    private EmbeddingModel createEmbeddingModel(String kbId) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
        String vectorModel = kb != null && kb.getVectorModel() != null ? kb.getVectorModel() : "openai";
        
        if ("openai".equals(vectorModel) && openaiApiKey != null && !openaiApiKey.isEmpty()) {
            return OpenAiEmbeddingModel.builder()
                    .apiKey(openaiApiKey)
                    .modelName(openaiEmbeddingModel)
                    .build();
        } else {
            // 使用默认的模拟模型
            return new MockEmbeddingModel();
        }
    }

    /**
     * 更新文档状态
     */
    private void updateDocumentStatus(String kbId, String title, String status) {
        try {
            VectorDocument doc = vectorDocumentMapper.selectByKbIdAndTitle(kbId, title);
            if (doc != null) {
                doc.setStatus(status);
                doc.setUpdatedAt(LocalDateTime.now());
                vectorDocumentMapper.updateById(doc);
            }
        } catch (Exception e) {
            log.error("更新文档状态失败: {}", title, e);
        }
    }

    /**
     * 获取文件类型
     */
    private String getFileType(String fileName) {
        if (fileName == null) return "text";
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension;
    }

    /**
     * 模拟嵌入模型（用于测试）
     */
    private static class MockEmbeddingModel implements EmbeddingModel {
        @Override
        public Response<Embedding> embed(String text) {
            // 生成随机向量用于测试
            List<Float> values = new ArrayList<>();
            for (int i = 0; i < 1536; i++) {
                values.add((float) (Math.random() - 0.5));
            }
            return Response.from(Embedding.from(values));
        }

        @Override
        public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
            List<Embedding> embeddings = textSegments.stream()
                    .map(segment -> embed(segment.text()).content())
                    .toList();
            return Response.from(embeddings);
        }
    }
}
