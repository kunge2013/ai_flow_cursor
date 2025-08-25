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

    @Data
    public static class DocumentVectorRequest {
        private String documentId;
        private String kbId;
        private String content;
        private String vectorModel;
        private String metadata;
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