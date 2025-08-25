package com.aiflow.server.service;

import com.aiflow.server.entity.NodeTypeEntity;
import com.aiflow.server.mapper.NodeTypeMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeTypeService {

    private final NodeTypeMapper nodeTypeMapper;

    /**
     * 查询所有可用的节点类型
     */
    public List<NodeTypeEntity> listAllNodeTypes() {
        QueryWrapper<NodeTypeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("enabled", true);
        wrapper.orderBy(true, true, "category", "type_code");
        return nodeTypeMapper.selectList(wrapper);
    }

    /**
     * 根据分类查询节点类型
     */
    public List<NodeTypeEntity> listNodeTypesByCategory(String category) {
        QueryWrapper<NodeTypeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("enabled", true);
        if (category != null && !category.isBlank()) {
            wrapper.eq("category", category);
        }
        wrapper.orderBy(true, true, "type_code");
        return nodeTypeMapper.selectList(wrapper);
    }

    /**
     * 根据类型代码查询节点类型
     */
    public NodeTypeEntity getNodeTypeByCode(String typeCode) {
        QueryWrapper<NodeTypeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("type_code", typeCode);
        wrapper.eq("enabled", true);
        return nodeTypeMapper.selectOne(wrapper);
    }

    /**
     * 查询所有分类
     */
    public List<String> listCategories() {
        QueryWrapper<NodeTypeEntity> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT category");
        wrapper.eq("enabled", true);
        wrapper.isNotNull("category");
        wrapper.ne("category", "");
        
        return nodeTypeMapper.selectObjs(wrapper)
                .stream()
                .map(Object::toString)
                .sorted()
                .toList();
    }
} 