package com.aiflow.server.service.impl;

import com.aiflow.aimodel.factory.AiModelFactory;
import com.aiflow.aimodel.adapter.AiModelAdapter;
import com.aiflow.aimodel.model.AiModelConfig;
import com.aiflow.aimodel.model.AiModelType;
import com.aiflow.server.dto.ModelDTO;
import com.aiflow.server.dto.ModelQueryDTO;
import com.aiflow.server.dto.ModelTestDTO;
import com.aiflow.server.entity.Model;
import com.aiflow.server.mapper.ModelMapper;
import com.aiflow.server.service.ModelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模型服务实现类
 */
@Slf4j
@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService {

    @Autowired
    private AiModelFactory aiModelFactory;

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
            Model model = getById(testDTO.getModelId());
            // 根据模型类型获取对应的适配器
            String modelType = determineModelType(model.getApiEndpoint());
            AiModelType aiModelType = AiModelType.fromCode(modelType);
            AiModelAdapter adapter = aiModelFactory.getAdapter(aiModelType);

            // 创建 AiModelConfig 对象
            AiModelConfig config = AiModelConfig.builder()
                    .type(aiModelType)
                    .apiEndpoint(model.getApiEndpoint())
                    .apiKey(model.getApiKey())
                    .temperature(model.getTemperature())
                    .maxTokens(model.getMaxTokens())
                    .topP(model.getTopP())
                    .baseModel(model.getModelType()) // 使用默认值
                    .build();
            
            log.info("使用适配器 {} 测试模型连接", adapter.getSupportedType());
            return adapter.testConnection(config).get();
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
        return aiModelFactory.getAvailableModelTypes().stream()
                .map(AiModelType::getCode)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public String generateTextWithAiModel(Long modelId, String prompt, Integer maxTokens, Double temperature) {
        ModelDTO modelDTO = getModelById(modelId);
        if (modelDTO == null) {
            throw new RuntimeException("模型不存在，ID: " + modelId);
        }
        
        // 根据模型类型获取对应的适配器
        String modelType = determineModelType(modelDTO.getApiEndpoint());
        AiModelType aiModelType = AiModelType.fromCode(modelType);
        AiModelAdapter adapter = aiModelFactory.getAdapter(aiModelType);
        
        // 创建 AiModelConfig 对象
        AiModelConfig config = AiModelConfig.builder()
                .type(aiModelType)
                .apiEndpoint(modelDTO.getApiEndpoint())
                .apiKey(modelDTO.getApiKey())
                .baseModel(modelDTO.getBaseModel())
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();
        
        log.info("使用适配器 {} 生成文本", adapter.getSupportedType());
        return adapter.generateText(config, prompt);
    }

    @Override
    public boolean validateAiModelConfig(ModelDTO modelDTO) {
        try {
            // 根据模型类型获取对应的适配器
            String modelType = determineModelType(modelDTO.getApiEndpoint());
            AiModelType aiModelType = AiModelType.fromCode(modelType);
            AiModelAdapter adapter = aiModelFactory.getAdapter(aiModelType);
            
            // 创建 AiModelConfig 对象
            AiModelConfig config = AiModelConfig.builder()
                    .type(aiModelType)
                    .apiEndpoint(modelDTO.getApiEndpoint())
                    .apiKey(modelDTO.getApiKey())
                    .baseModel(modelDTO.getBaseModel())
                    .temperature(modelDTO.getTemperature())
                    .maxTokens(modelDTO.getMaxTokens())
                    .topP(modelDTO.getTopP())
                    .build();
            
            return adapter.isValidConfig(config);
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
            return "zhipu-ai";
        } else if (endpoint.contains("deepseek")) {
            return "deepseek";
        } else if (endpoint.contains("openai") || endpoint.contains("api.openai.com")) {
            return "openai";
        } else if (endpoint.contains("anthropic") || endpoint.contains("claude")) {
            return "anthropic";
        } else if (endpoint.contains("gemini") || endpoint.contains("google")) {
            return "google-gemini";
        } else if (endpoint.contains("qianfan") || endpoint.contains("dashscope")) {
            return "qianfan";
        } else if (endpoint.contains("ollama")) {
            return "ollama";
        } else {
            return "openai"; // 默认类型
        }
    }
} 