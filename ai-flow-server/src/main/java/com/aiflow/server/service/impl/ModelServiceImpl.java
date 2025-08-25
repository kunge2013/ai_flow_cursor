package com.aiflow.server.service.impl;

import com.aiflow.server.ai.factory.AiModelAdapterFactory;
import com.aiflow.server.ai.adapter.AiModelAdapter;
import com.aiflow.server.dto.ModelDTO;
import com.aiflow.server.dto.ModelQueryDTO;
import com.aiflow.server.dto.ModelTestDTO;
import com.aiflow.server.entity.Model;
import com.aiflow.server.mapper.ModelMapper;
import com.aiflow.server.service.ModelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 模型服务实现类
 */
@Slf4j
@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService {

    @Autowired
    private AiModelAdapterFactory aiModelAdapterFactory;

    @Override
    public IPage<Model> getModelPage(ModelQueryDTO queryDTO) {
        Page<Model> page = new Page<>(queryDTO.getCurrentPage(), queryDTO.getPageSize());
        
        QueryWrapper<Model> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getModelName())) {
            queryWrapper.like("model_name", queryDTO.getModelName());
        }
        
        if (StringUtils.hasText(queryDTO.getModelType())) {
            queryWrapper.eq("model_type", queryDTO.getModelType());
        }
        
        if (StringUtils.hasText(queryDTO.getBaseModel())) {
            queryWrapper.eq("base_model", queryDTO.getBaseModel());
        }
        
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq("status", queryDTO.getStatus());
        }
        
        queryWrapper.orderByDesc("created_at");
        
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean createModel(ModelDTO modelDTO) {
        // 验证AI模型配置
        if (!validateAiModelConfig(modelDTO)) {
            log.error("AI模型配置验证失败: {}", modelDTO);
            return false;
        }
        
        Model model = new Model();
        BeanUtils.copyProperties(modelDTO, model);
        model.setCreatedAt(LocalDateTime.now());
        model.setUpdatedAt(LocalDateTime.now());
        
        return this.save(model);
    }

    @Override
    public boolean updateModel(ModelDTO modelDTO) {
        // 验证AI模型配置
        if (!validateAiModelConfig(modelDTO)) {
            log.error("AI模型配置验证失败: {}", modelDTO);
            return false;
        }
        
        Model model = this.getById(modelDTO.getId());
        if (model == null) {
            log.error("模型不存在，ID: {}", modelDTO.getId());
            return false;
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
            // 根据模型类型获取对应的适配器
            String modelType = determineModelType(testDTO.getApiEndpoint());
            AiModelAdapter adapter = aiModelAdapterFactory.getAdapter(modelType);
            
            log.info("使用适配器 {} 测试模型连接", adapter.getModelType());
            return adapter.testConnection(testDTO);
        } catch (Exception e) {
            log.error("测试模型失败", e);
            return "测试失败: " + e.getMessage();
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

    @Override
    public List<String> getAvailableAiModelTypes() {
        return aiModelAdapterFactory.getAvailableModelTypes();
    }

    @Override
    public String generateTextWithAiModel(Long modelId, String prompt, Integer maxTokens, Double temperature) {
        ModelDTO modelDTO = getModelById(modelId);
        if (modelDTO == null) {
            throw new RuntimeException("模型不存在，ID: " + modelId);
        }
        
        // 根据模型类型获取对应的适配器
        String modelType = determineModelType(modelDTO.getApiEndpoint());
        AiModelAdapter adapter = aiModelAdapterFactory.getAdapter(modelType);
        
        log.info("使用适配器 {} 生成文本", adapter.getModelType());
        return adapter.generateText(modelDTO, prompt, maxTokens, temperature);
    }

    @Override
    public boolean validateAiModelConfig(ModelDTO modelDTO) {
        try {
            // 根据模型类型获取对应的适配器
            String modelType = determineModelType(modelDTO.getApiEndpoint());
            AiModelAdapter adapter = aiModelAdapterFactory.getAdapter(modelType);
            
            return adapter.isValidConfig(modelDTO);
        } catch (Exception e) {
            log.error("验证AI模型配置失败", e);
            return false;
        }
    }

    /**
     * 根据API地址确定模型类型
     *
     * @param apiEndpoint API地址
     * @return 模型类型
     */
    private String determineModelType(String apiEndpoint) {
        if (apiEndpoint == null) {
            return "openai"; // 默认类型
        }
        
        String endpoint = apiEndpoint.toLowerCase();
        
        if (endpoint.contains("zhipu") || endpoint.contains("bigmodel.cn")) {
            return "zhipu";
        } else if (endpoint.contains("deepseek")) {
            return "deepseek";
        } else if (endpoint.contains("openai") || endpoint.contains("api.openai.com")) {
            return "openai";
        } else if (endpoint.contains("anthropic") || endpoint.contains("claude")) {
            return "claude";
        } else if (endpoint.contains("gemini") || endpoint.contains("google")) {
            return "gemini";
        } else if (endpoint.contains("qianfan") || endpoint.contains("dashscope")) {
            return "qianfan";
        } else {
            return "openai"; // 默认类型
        }
    }
} 