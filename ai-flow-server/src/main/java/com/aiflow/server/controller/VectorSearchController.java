package com.aiflow.server.controller;

import com.aiflow.server.dto.VectorSearchDtos.*;
import com.aiflow.server.service.VectorSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vector")
@Tag(name = "VectorSearch", description = "向量检索管理")
@RequiredArgsConstructor
public class VectorSearchController {

    private final VectorSearchService vectorSearchService;

    @PostMapping("/embed")
    @Operation(summary = "向量化文档")
    public ResponseEntity<VectorEmbeddingResponse> embedDocument(
            @Valid @RequestBody VectorEmbeddingRequest request) {
        VectorEmbeddingResponse response = vectorSearchService.embedDocument(
            request.getContent(), request.getVectorModel());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @Operation(summary = "向量相似度搜索")
    public ResponseEntity<VectorSearchResponse> searchSimilar(
            @Valid @RequestBody VectorSearchRequest request) {
        VectorSearchResponse response = vectorSearchService.searchSimilar(
            request.getQuery(), 
            request.getKbId(), 
            request.getTopK(), 
            request.getScoreThreshold());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch-embed")
    @Operation(summary = "批量向量化文档")
    public ResponseEntity<List<VectorEmbeddingResponse>> batchEmbedDocuments(
            @Valid @RequestBody BatchEmbeddingRequest request) {
        List<VectorEmbeddingResponse> responses = vectorSearchService.batchEmbedDocuments(
            request.getContents(), request.getVectorModel());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/document/save")
    @Operation(summary = "保存文档向量")
    public ResponseEntity<Void> saveDocumentVector(
            @Valid @RequestBody DocumentVectorRequest request) {
        vectorSearchService.saveDocumentVector(
            request.getDocumentId(), 
            request.getKbId(), 
            request.getContent(), 
            request.getVectorModel());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/document/{documentId}")
    @Operation(summary = "删除文档向量")
    public ResponseEntity<Void> deleteDocumentVector(
            @Parameter(description = "文档ID") @PathVariable String documentId) {
        vectorSearchService.deleteDocumentVector(documentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/document/{documentId}")
    @Operation(summary = "更新文档向量")
    public ResponseEntity<Void> updateDocumentVector(
            @Parameter(description = "文档ID") @PathVariable String documentId,
            @Valid @RequestBody DocumentVectorRequest request) {
        vectorSearchService.updateDocumentVector(
            documentId, request.getContent(), request.getVectorModel());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    @Operation(summary = "向量服务健康检查")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "VectorSearchService",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }
} 