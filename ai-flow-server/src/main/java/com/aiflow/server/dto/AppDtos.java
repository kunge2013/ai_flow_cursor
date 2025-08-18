package com.aiflow.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class AppDtos {

    public static class AiAppSummary {
        public String id;
        public String name;
        public String description;
        public String iconUrl;
        @NotBlank public String appType; // simple|advanced
        public String model;
        public String prompt;
        public String openingRemark;
        public Instant createdAt;
        public Instant updatedAt;
    }

    public static class AiAppUpsertRequest {
        @NotBlank @Size(max = 100) public String name;
        @Size(max = 1000) public String description;
        public String iconUrl;
        @NotBlank public String appType; // simple|advanced
        public String model;
        public String prompt;
        public String openingRemark;
    }
} 