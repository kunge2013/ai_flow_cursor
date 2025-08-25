package com.aiflow.server.controller;

import com.aiflow.server.dto.KbDtos.*;
import com.aiflow.server.service.KbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kb")
@Tag(name = "KnowledgeBase", description = "知识库管理")
@RequiredArgsConstructor
public class KbController {

    private final KbService kbService;

    @GetMapping
    @Operation(summary = "查询知识库列表")
    public KnowledgeBasePageResponse list(
            @Parameter(description = "名称模糊匹配") @RequestParam(name = "name", required = false) String name,
            @Parameter(description = "向量模型") @RequestParam(name = "vectorModel", required = false) String vectorModel,
            @Parameter(description = "状态") @RequestParam(name = "status", required = false) Boolean status,
            @Parameter(description = "页码") @RequestParam(name = "page", defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        KnowledgeBaseQueryRequest req = new KnowledgeBaseQueryRequest();
        req.name = name;
        req.vectorModel = vectorModel;
        req.status = status;
        req.page = page;
        req.size = size;
        return kbService.list(req);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建知识库")
    public KnowledgeBaseSummary create(@Valid @RequestBody KnowledgeBaseUpsertRequest req) {
        return kbService.create(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取知识库详情")
    public KnowledgeBaseSummary get(@PathVariable("id") String id) {
        return kbService.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新知识库")
    public KnowledgeBaseSummary update(@PathVariable("id") String id, @Valid @RequestBody KnowledgeBaseUpsertRequest req) {
        return kbService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除知识库")
    public void delete(@PathVariable("id") String id) {
        kbService.delete(id);
    }

    // 文档管理接口
    @GetMapping("/{id}/documents")
    @Operation(summary = "获取知识库文档列表")
    public List<DocumentInfo> getDocuments(@PathVariable("id") String id) {
        return kbService.getDocuments(id);
    }

    @PostMapping("/{id}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "添加文档到知识库")
    public DocumentInfo addDocument(
            @PathVariable("id") String id,
            @Valid @RequestBody DocumentUploadRequest req
    ) {
        return kbService.addDocument(id, req);
    }

    @DeleteMapping("/{id}/documents/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除知识库文档")
    public void deleteDocument(
            @PathVariable("id") String id,
            @PathVariable("documentId") String documentId
    ) {
        kbService.deleteDocument(id, documentId);
    }

    // 命中测试接口
    @PostMapping("/{id}/test")
    @Operation(summary = "知识库命中测试")
    public TestQueryResponse testQuery(
            @PathVariable("id") String id,
            @Valid @RequestBody TestQueryRequest req
    ) {
        return kbService.testQuery(id, req);
    }
} 