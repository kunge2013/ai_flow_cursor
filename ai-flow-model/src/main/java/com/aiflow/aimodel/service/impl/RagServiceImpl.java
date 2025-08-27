package com.aiflow.aimodel.service.impl;

import com.aiflow.aimodel.service.RagService;
import com.aiflow.aimodel.service.RagService.DocumentProcessResult;
import com.aiflow.aimodel.service.VectorService;
import com.aiflow.aimodel.service.VectorService.VectorSearchResult;
import com.aiflow.aimodel.service.VectorService.VectorStatistics;
import com.aiflow.aimodel.embedding.EmbeddingModel;
import com.aiflow.aimodel.vectorstore.VectorStore;
import com.aiflow.aimodel.factory.EmbeddingModelFactory;
import com.aiflow.aimodel.factory.VectorStoreFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RAG服务实现类
 * 基于LangChain4j实现检索增强生成功能
 */
@Slf4j
@Service
public class RagServiceImpl implements RagService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final EmbeddingModelFactory embeddingModelFactory;
    private final VectorStoreFactory vectorStoreFactory;
    private final VectorService vectorService;
    
    @Value("${ai.model.openai.api-key:}")
    private String openaiApiKey;
    
    @Value("${ai.model.zhipu-ai.api-key:}")
    private String zhipuApiKey;

    public RagServiceImpl(EmbeddingModelFactory embeddingModelFactory, 
                         VectorStoreFactory vectorStoreFactory,
                         VectorService vectorService) {
        this.embeddingModelFactory = embeddingModelFactory;
        this.vectorStoreFactory = vectorStoreFactory;
        this.vectorService = vectorService;
    }

    @Override
    public DocumentProcessResult processDocument(String kbId, String fileName, String content, String contentType) {
        long startTime = System.currentTimeMillis();
        DocumentProcessResult result = new DocumentProcessResult();
        
        try {
            log.info("开始处理文档: {}, 知识库: {}", fileName, kbId);
            
            // 1. 处理文档内容
            result = processDocumentContent(kbId, fileName, content, contentType);
            
            // 2. 异步进行RAG处理
            CompletableFuture.runAsync(() -> {
                try {
                    performRagProcessing(kbId, fileName, content);
                } catch (Exception e) {
                    log.error("RAG处理失败: {}", fileName, e);
                }
            }, executorService);
            
            result.setProcessingTime(System.currentTimeMillis() - startTime);
            result.setStatus("success");
            result.setMessage("文档处理成功，RAG处理已启动");
            
        } catch (Exception e) {
            log.error("文档处理失败: {}", fileName, e);
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
            
            // 1. 生成文档ID
            String documentId = generateDocumentId(kbId, title);
            result.setDocumentId(documentId);
            
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
    public VectorSearchResult searchSimilar(String query, String kbId, int topK, double scoreThreshold) {
        try {
            log.info("开始RAG相似度搜索: {}, 知识库: {}", query, kbId);
            
            // 使用向量服务进行搜索
            VectorSearchResult result = vectorService.searchSimilar(query, kbId, topK, scoreThreshold);
            
            if (result != null) {
                log.info("RAG搜索完成，找到结果: {}", result.getTitle());
            } else {
                log.info("RAG搜索完成，未找到相似结果");
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("RAG搜索失败", e);
            throw new RuntimeException("RAG搜索失败: " + e.getMessage());
        }
    }

    @Override
    public VectorStatistics getRagStatistics(String kbId) {
        try {
            log.info("获取RAG统计信息，知识库: {}", kbId);
            
            // 使用向量服务获取统计信息
            VectorStatistics stats = vectorService.getVectorStatistics(kbId);
            
            log.info("RAG统计信息获取成功，文档数量: {}", stats.getTotalDocuments());
            return stats;
            
        } catch (Exception e) {
            log.error("获取RAG统计信息失败: {}", kbId, e);
            throw new RuntimeException("获取RAG统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 执行RAG处理
     */
    private void performRagProcessing(String kbId, String title, String content) {
        try {
            log.info("开始RAG处理: {}", title);
            
            // 1. 获取向量存储 - 使用默认的 milvus 存储类型
            VectorStore vectorStore = vectorStoreFactory.getVectorStore("milvus");
            
            // 2. 获取嵌入模型
            String vectorModel = getVectorModelByKbId(kbId);
            EmbeddingModel embeddingModel = embeddingModelFactory.getEmbeddingModel(vectorModel);
            
            // 3. 文本分割
            List<String> chunks = splitTextIntoChunks(content);
            
            // 4. 处理每个chunk
            for (int i = 0; i < chunks.size(); i++) {
                String chunk = chunks.get(i);
                String chunkId = generateChunkId(kbId, title, i);
                
                // 生成向量
                var embedding = embeddingModel.embed(chunk);
                
                // 保存到向量存储
                saveChunkToVectorStore(vectorStore, chunkId, chunk, embedding, kbId, title);
            }
            
            log.info("RAG处理完成: {}，生成了 {} 个chunks", title, chunks.size());
            
        } catch (Exception e) {
            log.error("RAG处理失败: {}", title, e);
        }
    }

    /**
     * 文本分割
     */
    private List<String> splitTextIntoChunks(String content) {
        List<String> chunks = new ArrayList<>();
        
        // 简单的按句子分割，实际应用中可以使用更复杂的算法
        String[] sentences = content.split("[.!?。！？]");
        
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (!sentence.isEmpty()) {
                // 如果句子太长，进一步分割
                if (sentence.length() > 1000) {
                    chunks.addAll(splitLongSentence(sentence));
                } else {
                    chunks.add(sentence);
                }
            }
        }
        
        return chunks;
    }

    /**
     * 分割长句子
     */
    private List<String> splitLongSentence(String sentence) {
        List<String> subChunks = new ArrayList<>();
        int maxLength = 1000;
        
        for (int i = 0; i < sentence.length(); i += maxLength) {
            int end = Math.min(i + maxLength, sentence.length());
            subChunks.add(sentence.substring(i, end));
        }
        
        return subChunks;
    }

    /**
     * 保存chunk到向量存储
     */
    private void saveChunkToVectorStore(VectorStore vectorStore, String chunkId, String content, 
                                       Object embedding, String kbId, String title) {
        try {
            // 这里需要根据实际的VectorStore接口实现
            // 暂时记录日志
            log.debug("保存chunk到向量存储: {}, 长度: {}", chunkId, content.length());
            
        } catch (Exception e) {
            log.error("保存chunk到向量存储失败: {}", chunkId, e);
        }
    }

    /**
     * 生成文档ID
     */
    private String generateDocumentId(String kbId, String title) {
        return kbId + "_" + title.hashCode() + "_" + System.currentTimeMillis();
    }

    /**
     * 生成chunk ID
     */
    private String generateChunkId(String kbId, String title, int index) {
        return kbId + "_" + title.hashCode() + "_chunk_" + index;
    }

    /**
     * 获取知识库的向量模型
     */
    private String getVectorModelByKbId(String kbId) {
        try {
            // 这里应该查询数据库获取向量模型
            // 目前返回默认值
            return "openai";
            
        } catch (Exception e) {
            log.error("获取向量模型失败，知识库ID: {}", kbId, e);
            return "openai";
        }
    }
}
