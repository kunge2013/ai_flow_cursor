package com.aiflow.server.controller;

import com.aiflow.server.dto.KbDtos.KnowledgeBaseSummary;
import com.aiflow.server.dto.KbDtos.KnowledgeBaseUpsertRequest;
import com.aiflow.server.service.KbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kb")
@Tag(name = "KnowledgeBase", description = "知识库管理")
public class KbController {

    private final KbService kbService = new KbService();

    @GetMapping
    @Operation(summary = "查询知识库列表")
    public List<KnowledgeBaseSummary> list(
            @Parameter(description = "名称模糊匹配") @RequestParam(name = "name", required = false) String name
    ) {
        return kbService.list(name);
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
} 