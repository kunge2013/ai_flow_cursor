package com.aiflow.server.controller;

import com.aiflow.server.dto.ModelDtos.ModelConfig;
import com.aiflow.server.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@Tag(name = "Model", description = "模型配置")
@RequiredArgsConstructor
public class ModelController {

    private final ModelService modelService;

    @GetMapping
    @Operation(summary = "查询模型列表")
    public List<ModelConfig> list(@Parameter(description = "是否启用") @RequestParam(name = "enabled", required = false) Boolean enabled) {
        return modelService.list(enabled);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建模型")
    public ModelConfig create(@RequestBody ModelConfig req) {
        return modelService.create(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取模型详情")
    public ModelConfig get(@PathVariable("id") String id) {
        return modelService.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新模型")
    public ModelConfig update(@PathVariable("id") String id, @RequestBody ModelConfig req) {
        return modelService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除模型")
    public void delete(@PathVariable("id") String id) {
        modelService.delete(id);
    }
} 