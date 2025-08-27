package com.aiflow.aimodel.dto;

import java.util.List;

/**
 * 向量相关的DTO类
 */
public class VectorDtos {

    /**
     * 向量搜索请求
     */
    public static class VectorSearchRequest {
        private String query;
        private String kbId;
        private int topK = 10;
        private double scoreThreshold = 0.7;
        
        // Getters and Setters
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        
        public int getTopK() { return topK; }
        public void setTopK(int topK) { this.topK = topK; }
        
        public double getScoreThreshold() { return scoreThreshold; }
        public void setScoreThreshold(double scoreThreshold) { this.scoreThreshold = scoreThreshold; }
    }

    /**
     * 向量搜索响应
     */
    public static class VectorSearchResponse {
        private String query;
        private String kbId;
        private Long totalCount;
        private List<VectorSearchResult> results;
        private long searchTime;
        
        // Getters and Setters
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        
        public List<VectorSearchResult> getResults() { return results; }
        public void setResults(List<VectorSearchResult> results) { this.results = results; }
        
        public long getSearchTime() { return searchTime; }
        public void setSearchTime(long searchTime) { this.searchTime = searchTime; }
    }

    /**
     * 向量搜索结果
     */
    public static class VectorSearchResult {
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
     * 向量嵌入请求
     */
    public static class VectorEmbeddingRequest {
        private String content;
        private String vectorModel;
        private String documentId;
        private String kbId;
        
        // Getters and Setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getVectorModel() { return vectorModel; }
        public void setVectorModel(String vectorModel) { this.vectorModel = vectorModel; }
        
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
        
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
    }

    /**
     * 向量嵌入响应
     */
    public static class VectorEmbeddingResponse {
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
     * 文档向量请求
     */
    public static class DocumentVectorRequest {
        private String documentId;
        private String kbId;
        private String content;
        private String vectorModel;
        
        // Getters and Setters
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
        
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getVectorModel() { return vectorModel; }
        public void setVectorModel(String vectorModel) { this.vectorModel = vectorModel; }
    }

    /**
     * 文档向量信息
     */
    public static class DocumentVectorInfo {
        private String documentId;
        private VectorEmbeddingResponse embedding;
        private RagDocumentMetadata metadata;
        private long lastUpdated;
        
        // Getters and Setters
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
        
        public VectorEmbeddingResponse getEmbedding() { return embedding; }
        public void setEmbedding(VectorEmbeddingResponse embedding) { this.embedding = embedding; }
        
        public RagDocumentMetadata getMetadata() { return metadata; }
        public void setMetadata(RagDocumentMetadata metadata) { this.metadata = metadata; }
        
        public long getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
    }

    /**
     * RAG文档元数据
     */
    public static class RagDocumentMetadata {
        private String documentId;
        private int chunkCount;
        private String processingStatus;
        private String vectorModel;
        private long createdAt;
        
        // Getters and Setters
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
        
        public int getChunkCount() { return chunkCount; }
        public void setChunkCount(int chunkCount) { this.chunkCount = chunkCount; }
        
        public String getProcessingStatus() { return processingStatus; }
        public void setProcessingStatus(String processingStatus) { this.processingStatus = processingStatus; }
        
        public String getVectorModel() { return vectorModel; }
        public void setVectorModel(String vectorModel) { this.vectorModel = vectorModel; }
        
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }

    /**
     * 增强向量搜索响应
     */
    public static class EnhancedVectorSearchResponse {
        private String query;
        private String kbId;
        private List<VectorSearchResult> results;
        private long totalResults;
        private long searchTime;
        private String vectorModel;
        private SearchStatistics statistics;
        
        // Getters and Setters
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        
        public List<VectorSearchResult> getResults() { return results; }
        public void setResults(List<VectorSearchResult> results) { this.results = results; }
        
        public long getTotalResults() { return totalResults; }
        public void setTotalResults(long totalResults) { this.totalResults = totalResults; }
        
        public long getSearchTime() { return searchTime; }
        public void setSearchTime(long searchTime) { this.searchTime = searchTime; }
        
        public String getVectorModel() { return vectorModel; }
        public void setVectorModel(String vectorModel) { this.vectorModel = vectorModel; }
        
        public SearchStatistics getStatistics() { return statistics; }
        public void setStatistics(SearchStatistics statistics) { this.statistics = statistics; }
    }

    /**
     * 搜索统计信息
     */
    public static class SearchStatistics {
        private int queryLength;
        private int resultCount;
        private double averageScore;
        
        // Getters and Setters
        public int getQueryLength() { return queryLength; }
        public void setQueryLength(int queryLength) { this.queryLength = queryLength; }
        
        public int getResultCount() { return resultCount; }
        public void setResultCount(int resultCount) { this.resultCount = resultCount; }
        
        public double getAverageScore() { return averageScore; }
        public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    }
}
