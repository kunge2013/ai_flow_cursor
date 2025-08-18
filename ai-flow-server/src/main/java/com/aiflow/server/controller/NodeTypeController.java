package com.aiflow.server.controller;

import com.aiflow.server.entity.NodeTypeEntity;
import com.aiflow.server.service.NodeTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Node Type", description = "节点类型管理API")
@RestController
@RequestMapping("/api/node-types")
@RequiredArgsConstructor
public class NodeTypeController {

    private final NodeTypeService nodeTypeService;

    @Operation(summary = "查询所有可用节点类型", description = "获取所有启用的节点类型列表")
    @GetMapping
    public List<NodeTypeEntity> listAllNodeTypes() {
        return nodeTypeService.listAllNodeTypes();
    }

    @Operation(summary = "根据分类查询节点类型", description = "根据指定分类查询节点类型")
    @GetMapping("/category/{category}")
    public List<NodeTypeEntity> listNodeTypesByCategory(
            @Parameter(description = "节点分类") @PathVariable String category) {
        return nodeTypeService.listNodeTypesByCategory(category);
    }

    @Operation(summary = "查询节点分类列表", description = "获取所有可用的节点分类")
    @GetMapping("/categories")
    public List<String> listCategories() {
        return nodeTypeService.listCategories();
    }

    @Operation(summary = "根据类型代码查询节点类型", description = "根据类型代码获取具体节点类型信息")
    @GetMapping("/{typeCode}")
    public NodeTypeEntity getNodeTypeByCode(
            @Parameter(description = "节点类型代码") @PathVariable String typeCode) {
        return nodeTypeService.getNodeTypeByCode(typeCode);
    }
} 