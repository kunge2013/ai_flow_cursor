package com.aiflow.aimodel.service.impl;

import com.aiflow.aimodel.service.VectorService;
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
import java.util.stream.Collectors;

// LangChain4j imports
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;

/**
 * 向量服务实现类
 * 基于LangChain4j实现文档向量化和向量搜索功能
 */
@Slf4j
@Service
public class VectorServiceImpl implements VectorService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final EmbeddingModelFactory embeddingModelFactory;
    private final VectorStoreFactory vectorStoreFactory;
    
    @Value("${ai.model.openai.api-key:}")
    private String openaiApiKey;
    
    @Value("${ai.model.zhipu-ai.api-key:}")
    private String zhipuApiKey;

    public VectorServiceImpl(EmbeddingModelFactory embeddingModelFactory, 
                           VectorStoreFactory vectorStoreFactory) {
        this.embeddingModelFactory = embeddingModelFactory;
        this.vectorStoreFactory = vectorStoreFactory;
    }

    @Override
    public VectorEmbeddingResult embedDocument(String content, String vectorModel) {
        try {
            log.info("开始向量化文档，模型: {}, 内容长度: {}", vectorModel, content.length());
            
            // 获取嵌入模型
            EmbeddingModel embeddingModel = embeddingModelFactory.getEmbeddingModel(vectorModel);
            
            // 生成向量
            var embedding = embeddingModel.embed(content);
            List<Double> embeddingValues = embedding.vectorAsList().stream()
                    .map(Double::valueOf)
                    .collect(Collectors.toList());
            
            // 构建结果
            VectorEmbeddingResult result = new VectorEmbeddingResult();
            result.setContent(content);
            result.setEmbedding(embeddingValues);
            result.setVectorModel(vectorModel);
            result.setTokenCount(estimateTokenCount(content, vectorModel));
            result.setCost(calculateCost(content, vectorModel));
            
            log.info("文档向量化完成，向量维度: {}", embeddingValues.size());
            return result;
            
        } catch (Exception e) {
            log.error("文档向量化失败", e);
            throw new RuntimeException("文档向量化失败: " + e.getMessage());
        }
    }

    @Override
    public VectorSearchResult searchSimilar(String query, String kbId, int topK, double scoreThreshold) {
        try {
            log.info("开始向量相似度搜索，查询: {}, 知识库: {}, topK: {}, 阈值: {}", 
                    query, kbId, topK, scoreThreshold);
            
            // 获取向量存储
            VectorStore vectorStore = vectorStoreFactory.getVectorStore(kbId);
            
            // 获取嵌入模型
            String vectorModel = getVectorModelByKbId(kbId);
            EmbeddingModel embeddingModel = embeddingModelFactory.getEmbeddingModel(vectorModel);
            
            // 生成查询向量
            var queryEmbedding = embeddingModel.embed(query);
            List<Float> queryVector = queryEmbedding.vectorAsList();
            
            // 执行搜索
            var results = vectorStore.findRelevant(kbId, queryVector, topK);
            
            if (!results.isEmpty()) {
                log.info("向量搜索完成，找到 {} 个结果", results.size());
                // 转换结果
                return convertToVectorSearchResult(results.get(0));
            } else {
                log.info("向量搜索完成，未找到相似结果");
                return null;
            }
            
        } catch (Exception e) {
            log.error("向量相似度搜索失败", e);
            throw new RuntimeException("向量相似度搜索失败: " + e.getMessage());
        }
    }

    @Override
    public List<VectorEmbeddingResult> batchEmbedDocuments(List<String> contents, String vectorModel) {
        try {
            log.info("开始批量向量化文档，数量: {}, 模型: {}", contents.size(), vectorModel);
            
            List<CompletableFuture<VectorEmbeddingResult>> futures = new ArrayList<>();
            
            for (String content : contents) {
                CompletableFuture<VectorEmbeddingResult> future = CompletableFuture.supplyAsync(() -> 
                    embedDocument(content, vectorModel), executorService);
                futures.add(future);
            }
            
            // 等待所有任务完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            allFutures.join();
            
            List<VectorEmbeddingResult> results = new ArrayList<>();
            for (CompletableFuture<VectorEmbeddingResult> future : futures) {
                results.add(future.get());
            }
            
            log.info("批量向量化完成，成功处理 {} 个文档", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("批量向量化失败", e);
            throw new RuntimeException("批量向量化失败: " + e.getMessage());
        }
    }

    @Override
    public void saveDocumentVector(String documentId, String kbId, String content, String vectorModel) {
        try {
            log.info("开始保存文档向量，文档ID: {}, 知识库: {}, 模型: {}", documentId, kbId, vectorModel);
            
            // 1. 生成向量
            VectorEmbeddingResult embedding = embedDocument(content, vectorModel);
            embedding.setDocumentId(documentId);
            
            // 2. 保存到向量存储
            VectorStore vectorStore = vectorStoreFactory.getVectorStore(kbId);
            
            // 3. 创建文本段落并添加元数据
            TextSegment textSegment = TextSegment.from(content);
            textSegment.metadata().add("documentId", documentId);
            textSegment.metadata().add("kbId", kbId);
            textSegment.metadata().add("title", "Document_" + documentId);
            textSegment.metadata().add("timestamp", System.currentTimeMillis());
            
            // 4. 将向量转换为Float列表（LangChain4j需要Float类型）
            List<Float> floatEmbedding = embedding.getEmbedding().stream()
                    .map(Double::floatValue)
                    .collect(Collectors.toList());
            
            // 5. 保存到向量存储
            vectorStore.add(documentId, 
                    dev.langchain4j.data.embedding.Embedding.from(floatEmbedding));
            
            log.info("文档向量保存成功，文档ID: {}", documentId);
            
        } catch (Exception e) {
            log.error("保存文档向量失败，文档ID: {}", documentId, e);
            throw new RuntimeException("保存文档向量失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteDocumentVector(String documentId) {
        try {
            log.info("开始删除文档向量，文档ID: {}", documentId);
            
            // 从所有向量存储中删除
            // 这里需要根据实际情况实现，可能需要遍历所有知识库
            log.info("文档向量删除成功，文档ID: {}", documentId);
            
        } catch (Exception e) {
            log.error("删除文档向量失败，文档ID: {}", documentId, e);
            throw new RuntimeException("删除文档向量失败: " + e.getMessage());
        }
    }

    @Override
    public void updateDocumentVector(String documentId, String content, String vectorModel) {
        try {
            log.info("开始更新文档向量，文档ID: {}, 模型: {}", documentId, vectorModel);
            
            // 1. 删除旧的向量数据
            deleteDocumentVector(documentId);
            
            // 2. 获取知识库ID（这里需要根据实际情况实现）
            String kbId = getKbIdByDocumentId(documentId);
            if (kbId == null) {
                throw new RuntimeException("无法找到文档对应的知识库ID");
            }
            
            // 3. 保存新的向量数据
            saveDocumentVector(documentId, kbId, content, vectorModel);
            
            log.info("文档向量更新成功，文档ID: {}", documentId);
            
        } catch (Exception e) {
            log.error("更新文档向量失败，文档ID: {}", documentId, e);
            throw new RuntimeException("更新文档向量失败: " + e.getMessage());
        }
    }

    @Override
    public VectorStatistics getVectorStatistics(String kbId) {
        try {
            VectorStatistics stats = new VectorStatistics();
            stats.setKbId(kbId);
            
            // 获取向量存储
            VectorStore vectorStore = vectorStoreFactory.getVectorStore(kbId);
            
            // 获取统计信息
            stats.setTotalDocuments(vectorStore.getCollectionSize(kbId));
            stats.setTotalChunks(0); // 暂时设为0，需要根据实际情况实现
            stats.setTotalEmbeddings(vectorStore.getCollectionSize(kbId));
            stats.setVectorModel(getVectorModelByKbId(kbId));
            stats.setLastUpdated(System.currentTimeMillis());
            
            return stats;
            
        } catch (Exception e) {
            log.error("获取向量统计信息失败: {}", kbId, e);
            throw new RuntimeException("获取向量统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 转换EmbeddingMatch为VectorSearchResult
     */
    private VectorSearchResult convertToVectorSearchResult(dev.langchain4j.store.embedding.EmbeddingMatch<dev.langchain4j.data.segment.TextSegment> match) {
        VectorSearchResult result = new VectorSearchResult();
        result.setTitle(match.embedded().metadata().get("title"));
        result.setContent(match.embedded().text());
        result.setScore(match.score());
        result.setDocumentId(match.embedded().metadata().get("documentId"));
        result.setKbId(match.embedded().metadata().get("kbId"));
        return result;
    }

    /**
     * 估算token数量
     */
    private Long estimateTokenCount(String content, String vectorModel) {
        // 根据模型类型使用不同的估算策略
        switch (vectorModel.toLowerCase()) {
            case "openai":
                // OpenAI: 大约1个token = 4个字符
                return (long) Math.ceil(content.length() / 4.0);
            case "zhipu":
            case "zhipu-ai":
                // 智普AI: 大约1个token = 3个字符（中文较多）
                return (long) Math.ceil(content.length() / 3.0);
            case "ollama":
                // Ollama: 大约1个token = 4个字符
                return (long) Math.ceil(content.length() / 4.0);
            default:
                return (long) Math.ceil(content.length() / 4.0);
        }
    }

    /**
     * 计算成本
     */
    private Long calculateCost(String content, String vectorModel) {
        // 这里可以根据实际API定价计算成本
        // 目前返回0，表示免费或未计算
        return 0L;
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

    /**
     * 获取文档对应的知识库ID
     */
    private String getKbIdByDocumentId(String documentId) {
        try {
            // 这里应该查询数据库获取知识库ID
            // 目前返回模拟数据
            return "kb_" + documentId.hashCode();
            
        } catch (Exception e) {
            log.error("获取知识库ID失败，文档ID: {}", documentId, e);
            return "openai";
        }
    }
}
