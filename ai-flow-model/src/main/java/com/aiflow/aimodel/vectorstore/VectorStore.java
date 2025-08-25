package com.aiflow.aimodel.vectorstore;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.util.List;

/**
 * 向量存储抽象接口
 */
public interface VectorStore extends EmbeddingStore<TextSegment> {
    
    /**
     * 获取存储类型
     */
    String getStoreType();
    
    /**
     * 获取存储名称
     */
    String getStoreName();
    
    /**
     * 获取连接状态
     */
    boolean isConnected();
    
    /**
     * 测试连接
     */
    boolean testConnection();
    
    /**
     * 创建集合/索引
     */
    boolean createCollection(String collectionName);
    
    /**
     * 删除集合/索引
     */
    boolean deleteCollection(String collectionName);
    
    /**
     * 检查集合是否存在
     */
    boolean collectionExists(String collectionName);
    
    /**
     * 获取所有集合名称
     */
    List<String> listCollections();
    
    /**
     * 清空集合
     */
    boolean clearCollection(String collectionName);
    
    /**
     * 获取集合中的文档数量
     */
    long getCollectionSize(String collectionName);
    
    /**
     * 添加文档到集合
     */
    String addDocument(String collectionName, Document document);
    
    /**
     * 批量添加文档到集合
     */
    List<String> addDocuments(String collectionName, List<Document> documents);
    
    /**
     * 根据ID删除文档
     */
    boolean deleteDocument(String collectionName, String documentId);
    
    /**
     * 根据ID获取文档
     */
    Document getDocument(String collectionName, String documentId);
    
    /**
     * 相似性搜索
     */
    List<EmbeddingMatch<TextSegment>> findRelevant(String collectionName, String query, int maxResults);
    
    /**
     * 相似性搜索（使用向量）
     */
    List<EmbeddingMatch<TextSegment>> findRelevant(String collectionName, List<Float> queryEmbedding, int maxResults);
} 