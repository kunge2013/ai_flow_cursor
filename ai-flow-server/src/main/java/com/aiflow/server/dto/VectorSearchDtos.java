package com.aiflow.server.dto;

import lombok.Data;
import java.util.List;

public class VectorSearchDtos {

    @Data
    public static class VectorEmbeddingRequest {
        private String content;
        private String vectorModel;
    }

    @Data
    public static class VectorEmbeddingResponse {
        private String content;
        private List<Double> embedding;
        private String vectorModel;
        private Long tokenCount;
        private Long cost;
    }

    @Data
    public static class VectorSearchRequest {
        private String query;
        private String kbId;
        private Integer topK = 5;
        private Double scoreThreshold = 0.7;
    }

    @Data
    public static class VectorSearchResponse {
        private String query;
        private List<VectorSearchResult> results;
        private Long totalCount;
        private Long searchTime;
    }

    @Data
    public static class VectorSearchResult {
        private String documentId;
        private String title;
        private String content;
        private Double score;
        private String metadata;
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
        private long processingTime;
        private String vectorModel;

        // Getters and Setters
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }

        public int getChunkCount() { return chunkCount; }
        public void setChunkCount(int chunkCount) { this.chunkCount = chunkCount; }

        public String getProcessingStatus() { return processingStatus; }
        public void setProcessingStatus(String processingStatus) { this.processingStatus = processingStatus; }

        public long getProcessingTime() { return processingTime; }
        public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }

        public String getVectorModel() { return vectorModel; }
        public void setVectorModel(String vectorModel) { this.vectorModel = vectorModel; }
    }

    /**
     * 增强向量搜索响应
     */
    public static class EnhancedVectorSearchResponse {
        private String query;
        private String kbId;
        private List<VectorSearchResult> results;
        private int totalResults;
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

        public int getTotalResults() { return totalResults; }
        public void setTotalResults(int totalResults) { this.totalResults = totalResults; }

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
        private long searchDuration;

        // Getters and Setters
        public int getQueryLength() { return queryLength; }
        public void setQueryLength(int queryLength) { this.queryLength = queryLength; }

        public int getResultCount() { return resultCount; }
        public void setResultCount(int resultCount) { this.resultCount = resultCount; }

        public double getAverageScore() { return averageScore; }
        public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

        public long getSearchDuration() { return searchDuration; }
        public void setSearchDuration(long searchDuration) { this.searchDuration = searchDuration; }
    }

    @Data
    public static class BatchEmbeddingRequest {
        private List<String> contents;
        private String vectorModel;
    }

    @Data
    public static class BatchEmbeddingResponse {
        private List<VectorEmbeddingResponse> embeddings;
        private Long totalTokenCount;
        private Long totalCost;
    }
} 