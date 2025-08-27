package com.aiflow.aimodel.service;

import java.util.List;

/**
 * 向量服务接口
 * 提供文档向量化、向量搜索等核心功能
 */
public interface VectorService {
    
    /**
     * 向量化文档内容
     * @param content 文档内容
     * @param vectorModel 向量模型名称
     * @return 向量化结果
     */
    VectorEmbeddingResult embedDocument(String content, String vectorModel);
    
    /**
     * 向量相似度搜索
     * @param query 查询文本
     * @param kbId 知识库ID
     * @param topK 返回结果数量
     * @param scoreThreshold 相似度阈值
     * @return 搜索结果
     */
    VectorSearchResult searchSimilar(String query, String kbId, int topK, double scoreThreshold);
    
    /**
     * 批量向量化文档
     * @param contents 文档内容列表
     * @param vectorModel 向量模型名称
     * @return 向量化结果列表
     */
    List<VectorEmbeddingResult> batchEmbedDocuments(List<String> contents, String vectorModel);
    
    /**
     * 保存文档向量
     * @param documentId 文档ID
     * @param kbId 知识库ID
     * @param content 文档内容
     * @param vectorModel 向量模型名称
     */
    void saveDocumentVector(String documentId, String kbId, String content, String vectorModel);
    
    /**
     * 删除文档向量
     * @param documentId 文档ID
     */
    void deleteDocumentVector(String documentId);
    
    /**
     * 更新文档向量
     * @param documentId 文档ID
     * @param content 文档内容
     * @param vectorModel 向量模型名称
     */
    void updateDocumentVector(String documentId, String content, String vectorModel);
    
    /**
     * 获取向量统计信息
     * @param kbId 知识库ID
     * @return 统计信息
     */
    VectorStatistics getVectorStatistics(String kbId);
    
    /**
     * 向量嵌入结果
     */
    class VectorEmbeddingResult {
        private String content;
        private List<Double> embedding;
        private String vectorModel;
        private Long tokenCount;
        private Long cost;
        private String documentId;
        
        // Getters and Setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public List<Double> getEmbedding() { return embedding; }
        public void setEmbedding(List<Double> embedding) { this.embedding = embedding; }
        
        public String getVectorModel() { return vectorModel; }
        public void setVectorModel(String vectorModel) { this.vectorModel = vectorModel; }
        
        public Long getTokenCount() { return tokenCount; }
        public void setTokenCount(Long tokenCount) { this.tokenCount = tokenCount; }
        
        public Long getCost() { return cost; }
        public void setCost(Long cost) { this.cost = cost; }
        
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
    }
    
    /**
     * 向量搜索结果
     */
    class VectorSearchResult {
        private String title;
        private String content;
        private double score;
        private String documentId;
        private String kbId;
        
        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
        
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
    }
    
    /**
     * 向量统计信息
     */
    class VectorStatistics {
        private String kbId;
        private long totalDocuments;
        private long totalChunks;
        private long totalEmbeddings;
        private String vectorModel;
        private long lastUpdated;
        
        // Getters and Setters
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        
        public long getTotalDocuments() { return totalDocuments; }
        public void setTotalDocuments(long totalDocuments) { this.totalDocuments = totalDocuments; }
        
        public long getTotalChunks() { return totalChunks; }
        public void setTotalChunks(long totalChunks) { this.totalChunks = totalChunks; }
        
        public long getTotalEmbeddings() { return totalEmbeddings; }
        public void setTotalEmbeddings(long totalEmbeddings) { this.totalEmbeddings = totalEmbeddings; }
        
        public String getVectorModel() { return vectorModel; }
        public void setVectorModel(String vectorModel) { this.vectorModel = vectorModel; }
        
        public long getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
    }
} 