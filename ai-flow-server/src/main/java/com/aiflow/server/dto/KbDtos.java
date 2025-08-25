package com.aiflow.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public class KbDtos {

    public static class KnowledgeBaseSummary {
        public String id;
        public String name;
        public String description;
        public List<String> tags;
        public String vectorModel;
        public Boolean status;
        public Instant createdAt;
        public Instant updatedAt;
    }

    public static class KnowledgeBaseUpsertRequest {
        @NotBlank @Size(max = 100) public String name;
        @Size(max = 1000) public String description;
        public List<String> tags;
        public String vectorModel;
        public Boolean status;
    }

    public static class KnowledgeBaseQueryRequest {
        public String name;
        public String vectorModel;
        public Boolean status;
        public Integer page = 1;
        public Integer size = 10;
    }

    public static class KnowledgeBasePageResponse {
        public List<KnowledgeBaseSummary> records;
        public Long total;
        public Integer page;
        public Integer size;
    }

    public static class DocumentInfo {
        public String id;
        public String title;
        public String content;
        public String type;
        public Long size;
        public Instant createdAt;
        public Instant updatedAt;
    }

    public static class DocumentUploadRequest {
        @NotBlank public String title;
        @NotBlank public String content;
        public String type;
        public Long size;
    }

    public static class TestQueryRequest {
        @NotBlank public String query;
        public Integer topK = 5;
        public Double scoreThreshold = 0.7;
    }

    public static class TestQueryResponse {
        public String query;
        public List<DocumentInfo> documents;
        public List<Double> scores;
        public String answer;
    }
} 