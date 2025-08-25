package com.aiflow.aimodel.service;

import com.aiflow.aimodel.embedding.EmbeddingModel;
import com.aiflow.aimodel.factory.EmbeddingModelFactory;
import com.aiflow.aimodel.factory.VectorStoreFactory;
import com.aiflow.aimodel.vectorstore.VectorStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 向量服务
 */
@Slf4j
@Service
public class VectorService {
    
    private final EmbeddingModelFactory embeddingModelFactory;
    private final VectorStoreFactory vectorStoreFactory;
    
    @Autowired
    public VectorService(EmbeddingModelFactory embeddingModelFactory, VectorStoreFactory vectorStoreFactory) {
        this.embeddingModelFactory = embeddingModelFactory;
        this.vectorStoreFactory = vectorStoreFactory;
    }
    
    /**
     * 添加文档到向量存储
     */
    public String addDocument(String collectionName, Document document, String embeddingModelType) {
        try {
            // 获取向量模型
            EmbeddingModel embeddingModel = embeddingModelFactory.getEmbeddingModel(embeddingModelType);
            
            // 获取向量存储
            VectorStore vectorStore = vectorStoreFactory.getDefaultVectorStore();
            
            // 添加文档
            String documentId = vectorStore.addDocument(collectionName, document);
            
            log.info("文档添加成功，集合: {}, ID: {}, 向量模型: {}", collectionName, documentId, embeddingModelType);
            return documentId;
            
        } catch (Exception e) {
            log.error("添加文档失败: {}", e.getMessage(), e);
            throw new RuntimeException("添加文档失败", e);
        }
    }
    
    /**
     * 批量添加文档到向量存储
     */
    public List<String> addDocuments(String collectionName, List<Document> documents, String embeddingModelType) {
        try {
            // 获取向量模型
            EmbeddingModel embeddingModel = embeddingModelFactory.getEmbeddingModel(embeddingModelType);
            
            // 获取向量存储
            VectorStore vectorStore = vectorStoreFactory.getDefaultVectorStore();
            
            // 批量添加文档
            List<String> documentIds = vectorStore.addDocuments(collectionName, documents);
            
            log.info("批量文档添加成功，集合: {}, 数量: {}, 向量模型: {}", collectionName, documentIds.size(), embeddingModelType);
            return documentIds;
            
        } catch (Exception e) {
            log.error("批量添加文档失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量添加文档失败", e);
        }
    }
    
    /**
     * 相似性搜索
     */
    public List<EmbeddingMatch<TextSegment>> search(String collectionName, String query, int maxResults, String embeddingModelType) {
        try {
            // 获取向量模型
            EmbeddingModel embeddingModel = embeddingModelFactory.getEmbeddingModel(embeddingModelType);
            
            // 获取向量存储
            VectorStore vectorStore = vectorStoreFactory.getDefaultVectorStore();
            
            // 执行搜索
            List<EmbeddingMatch<TextSegment>> results = vectorStore.findRelevant(collectionName, query, maxResults);
            
            log.info("相似性搜索成功，集合: {}, 查询: {}, 结果数量: {}", collectionName, query, results.size());
            return results;
            
        } catch (Exception e) {
            log.error("相似性搜索失败: {}", e.getMessage(), e);
            throw new RuntimeException("相似性搜索失败", e);
        }
    }
    
    /**
     * 使用向量进行搜索
     */
    public List<EmbeddingMatch<TextSegment>> searchByVector(String collectionName, List<Float> queryEmbedding, int maxResults) {
        try {
            // 获取向量存储
            VectorStore vectorStore = vectorStoreFactory.getDefaultVectorStore();
            
            // 执行向量搜索
            List<EmbeddingMatch<TextSegment>> results = vectorStore.findRelevant(collectionName, queryEmbedding, maxResults);
            
            log.info("向量搜索成功，集合: {}, 结果数量: {}", collectionName, results.size());
            return results;
            
        } catch (Exception e) {
            log.error("向量搜索失败: {}", e.getMessage(), e);
            throw new RuntimeException("向量搜索失败", e);
        }
    }
    
    /**
     * 创建集合
     */
    public boolean createCollection(String collectionName) {
        try {
            VectorStore vectorStore = vectorStoreFactory.getDefaultVectorStore();
            boolean result = vectorStore.createCollection(collectionName);
            
            if (result) {
                log.info("集合创建成功: {}", collectionName);
            } else {
                log.warn("集合创建失败: {}", collectionName);
            }
            
            return result;
        } catch (Exception e) {
            log.error("创建集合失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 删除集合
     */
    public boolean deleteCollection(String collectionName) {
        try {
            VectorStore vectorStore = vectorStoreFactory.getDefaultVectorStore();
            boolean result = vectorStore.deleteCollection(collectionName);
            
            if (result) {
                log.info("集合删除成功: {}", collectionName);
            } else {
                log.warn("集合删除失败: {}", collectionName);
            }
            
            return result;
        } catch (Exception e) {
            log.error("删除集合失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 获取集合信息
     */
    public Map<String, Object> getCollectionInfo(String collectionName) {
        try {
            VectorStore vectorStore = vectorStoreFactory.getDefaultVectorStore();
            
            Map<String, Object> info = Map.of(
                "name", collectionName,
                "exists", vectorStore.collectionExists(collectionName),
                "size", vectorStore.getCollectionSize(collectionName),
                "storeType", vectorStore.getStoreType(),
                "storeName", vectorStore.getStoreName()
            );
            
            return info;
        } catch (Exception e) {
            log.error("获取集合信息失败: {}", e.getMessage(), e);
            return Map.of("error", e.getMessage());
        }
    }
    
    /**
     * 测试所有连接
     */
    public Map<String, Object> testConnections() {
        try {
            Map<String, Boolean> embeddingResults = embeddingModelFactory.testAllConnections();
            Map<String, Boolean> vectorStoreResults = vectorStoreFactory.testAllConnections();
            
            Map<String, Object> results = Map.of(
                "embeddingModels", embeddingResults,
                "vectorStores", vectorStoreResults,
                "defaultVectorStore", vectorStoreFactory.getDefaultStoreType()
            );
            
            return results;
        } catch (Exception e) {
            log.error("测试连接失败: {}", e.getMessage(), e);
            return Map.of("error", e.getMessage());
        }
    }
    
    /**
     * 获取所有可用的向量模型类型
     */
    public List<String> getAvailableEmbeddingModelTypes() {
        return embeddingModelFactory.getAllEmbeddingModelTypes();
    }
    
    /**
     * 获取所有可用的向量存储类型
     */
    public List<String> getAvailableVectorStoreTypes() {
        return vectorStoreFactory.getAllVectorStoreTypes();
    }
} 