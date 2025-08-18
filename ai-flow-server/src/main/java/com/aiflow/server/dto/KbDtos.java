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
        public Instant createdAt;
        public Instant updatedAt;
    }

    public static class KnowledgeBaseUpsertRequest {
        @NotBlank @Size(max = 100) public String name;
        @Size(max = 1000) public String description;
        public List<String> tags;
    }
} 