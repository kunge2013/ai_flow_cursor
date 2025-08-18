package com.aiflow.server.controller;

import com.aiflow.server.dto.FlowDtos.FlowGraph;
import com.aiflow.server.dto.FlowDtos.FlowRunRequest;
import com.aiflow.server.dto.FlowDtos.FlowRunResult;
import com.aiflow.server.dto.FlowDtos.FlowSummary;
import com.aiflow.server.dto.FlowDtos.FlowUpsertRequest;
import com.aiflow.server.dto.FlowDtos.FlowWithGraphResponse;
import com.aiflow.server.service.FlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flows")
@Tag(name = "Flow", description = "流程管理")
public class FlowController {

    private final FlowService flowService;

    public FlowController(FlowService flowService) {
        this.flowService = flowService;
    }

    @GetMapping
    @Operation(summary = "查询流程列表")
    public List<FlowSummary> listFlows(
            @Parameter(description = "名称模糊匹配") @RequestParam(name = "name", required = false) String name
    ) {
        return flowService.list(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建流程")
    public FlowSummary create(@Valid @RequestBody FlowUpsertRequest req) {
        return flowService.create(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取流程详情（含图）")
    public FlowWithGraphResponse get(@PathVariable("id") String id) {
        return flowService.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新流程基本信息")
    public FlowSummary update(@PathVariable("id") String id, @Valid @RequestBody FlowUpsertRequest req) {
        return flowService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除流程")
    public void delete(@PathVariable("id") String id) {
        flowService.delete(id);
    }

    @GetMapping("/{id}/graph")
    @Operation(summary = "获取流程图")
    public FlowGraph getGraph(@PathVariable("id") String id) {
        return flowService.getGraph(id);
    }

    @PutMapping("/{id}/graph")
    @Operation(summary = "保存流程图")
    public FlowGraph saveGraph(@PathVariable("id") String id, @Valid @RequestBody FlowGraph graph) {
        return flowService.saveGraph(id, graph);
    }

    @PostMapping("/{id}/run")
    @Operation(summary = "运行流程（调试）")
    public FlowRunResult run(@PathVariable("id") String id, @RequestBody FlowRunRequest req) {
        return flowService.run(id, req);
    }
} 