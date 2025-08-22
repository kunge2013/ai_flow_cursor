package com.aiflow.server.controller;

import com.aiflow.server.dto.ModelDTO;
import com.aiflow.server.dto.ModelQueryDTO;
import com.aiflow.server.dto.ModelTestDTO;
import com.aiflow.server.entity.Model;
import com.aiflow.server.service.ModelService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 模型管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
@Tag(name = "模型管理", description = "模型管理相关接口")
@Validated
public class ModelController {

    private final ModelService modelService;

    /**
     * 分页查询模型列表
     */
    @GetMapping
    @Operation(summary = "分页查询模型列表", description = "根据条件分页查询模型列表")
    public ResponseEntity<IPage<Model>> getModelPage(@Valid ModelQueryDTO queryDTO) {
        log.info("查询模型列表，参数: {}", queryDTO);
        IPage<Model> result = modelService.getModelPage(queryDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取模型详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取模型详情", description = "根据ID获取模型详细信息")
    public ResponseEntity<ModelDTO> getModelById(@PathVariable Long id) {
        log.info("获取模型详情，ID: {}", id);
        ModelDTO model = modelService.getModelById(id);
        if (model == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(model);
    }

    /**
     * 创建模型
     */
    @PostMapping
    @Operation(summary = "创建模型", description = "创建新的模型配置")
    public ResponseEntity<Boolean> createModel(@Valid @RequestBody ModelDTO modelDTO) {
        log.info("创建模型，参数: {}", modelDTO);
        boolean result = modelService.createModel(modelDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新模型
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新模型", description = "更新指定ID的模型信息")
    public ResponseEntity<Boolean> updateModel(@PathVariable Long id, @Valid @RequestBody ModelDTO modelDTO) {
        log.info("更新模型，ID: {}, 参数: {}", id, modelDTO);
        modelDTO.setId(id);
        boolean result = modelService.updateModel(modelDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除模型
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除模型", description = "删除指定ID的模型")
    public ResponseEntity<Boolean> deleteModel(@PathVariable Long id) {
        log.info("删除模型，ID: {}", id);
        boolean result = modelService.deleteModel(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 测试模型接口
     */
    @PostMapping("/test")
    @Operation(summary = "测试模型接口", description = "测试模型API接口是否可用")
    public ResponseEntity<String> testModel(@Valid @RequestBody ModelTestDTO testDTO) {
        log.info("测试模型接口，参数: {}", testDTO);
        String result = modelService.testModel(testDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取模型类型列表
     */
    @GetMapping("/types")
    @Operation(summary = "获取模型类型列表", description = "获取所有可用的模型类型")
    public ResponseEntity<String[]> getModelTypes() {
        String[] types = {"text", "image", "speech", "multimodal", "code", "translation"};
        return ResponseEntity.ok(types);
    }

    /**
     * 获取基础模型列表
     */
    @GetMapping("/base-models")
    @Operation(summary = "获取基础模型列表", description = "获取所有可用的基础模型")
    public ResponseEntity<String[]> getBaseModels() {
        String[] baseModels = {"gpt-4", "gpt-3.5", "claude", "gemini", "wenxin", "qwen"};
        return ResponseEntity.ok(baseModels);
    }
} 