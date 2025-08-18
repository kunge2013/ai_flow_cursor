package com.aiflow.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class FlowDtos {

    public static class FlowSummary {
        public String id;
        public String name;
        public String description;
        public Instant createdAt;
        public Instant updatedAt;
    }

    public static class FlowUpsertRequest {
        @NotBlank
        @Size(max = 100)
        public String name;
        @Size(max = 1000)
        public String description;
    }

    public static class FlowGraph {
        public List<GraphNode> nodes;
        public List<GraphEdge> edges;
    }

    public static class GraphNode {
        @NotBlank public String id;
        @NotBlank public String type;
        public Double x;
        public Double y;
        public String text;
        public Map<String, Object> properties;
    }

    public static class GraphEdge {
        public String id;
        @NotBlank public String sourceNodeId;
        @NotBlank public String targetNodeId;
        public String label;
    }

    public static class FlowWithGraphResponse {
        public String id;
        public String name;
        public String description;
        public Instant createdAt;
        public Instant updatedAt;
        public FlowGraph graph;
    }

    public static class FlowRunRequest {
        public Map<String, Object> inputs;
    }

    public static class FlowRunResult {
        public String flowId;
        public String runId;
        public Map<String, Object> outputs;
        public List<Map<String, Object>> trace;
    }
} 