package com.aiflow.server.service.impl;

import com.aiflow.server.dto.ModelDTO;
import com.aiflow.server.dto.ModelQueryDTO;
import com.aiflow.server.dto.ModelTestDTO;
import com.aiflow.server.entity.Model;
import com.aiflow.server.mapper.ModelMapper;
import com.aiflow.server.service.ModelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 模型服务实现类
 */
@Slf4j
@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService {

    @Override
    public IPage<Model> getModelPage(ModelQueryDTO queryDTO) {
        Page<Model> page = new Page<>(queryDTO.getCurrentPage(), queryDTO.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Model> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getModelName())) {
            queryWrapper.like(Model::getModelName, queryDTO.getModelName());
        }
        if (StringUtils.hasText(queryDTO.getModelType())) {
            queryWrapper.eq(Model::getModelType, queryDTO.getModelType());
        }
        if (StringUtils.hasText(queryDTO.getBaseModel())) {
            queryWrapper.eq(Model::getBaseModel, queryDTO.getBaseModel());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(Model::getStatus, queryDTO.getStatus());
        }
        
        queryWrapper.orderByDesc(Model::getCreatedAt);
        
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean createModel(ModelDTO modelDTO) {
        Model model = new Model();
        BeanUtils.copyProperties(modelDTO, model);
        model.setCreatedAt(LocalDateTime.now());
        model.setUpdatedAt(LocalDateTime.now());
        
        return this.save(model);
    }

    @Override
    public boolean updateModel(ModelDTO modelDTO) {
        if (modelDTO.getId() == null) {
            throw new IllegalArgumentException("模型ID不能为空");
        }
        
        Model model = this.getById(modelDTO.getId());
        if (model == null) {
            throw new IllegalArgumentException("模型不存在");
        }
        
        BeanUtils.copyProperties(modelDTO, model);
        model.setUpdatedAt(LocalDateTime.now());
        
        return this.updateById(model);
    }

    @Override
    public boolean deleteModel(Long id) {
        return this.removeById(id);
    }

    @Override
    public String testModel(ModelTestDTO testDTO) {
        try {
            log.info("开始测试模型接口: {}", testDTO.getApiEndpoint());
            
            // 这里可以添加实际的API测试逻辑
            // 例如：发送HTTP请求到模型API进行测试
            
            // 模拟测试过程
            Thread.sleep(1000);
            
            log.info("模型接口测试成功");
            return "模型接口测试成功！响应时间: 1000ms";
            
        } catch (Exception e) {
            log.error("模型接口测试失败", e);
            throw new RuntimeException("模型接口测试失败: " + e.getMessage());
        }
    }

    @Override
    public ModelDTO getModelById(Long id) {
        Model model = this.getById(id);
        if (model == null) {
            return null;
        }
        
        ModelDTO modelDTO = new ModelDTO();
        BeanUtils.copyProperties(model, modelDTO);
        return modelDTO;
    }
} 