package com.aiflow.aimodel.service;

import com.aiflow.aimodel.adapter.AiModelAdapter;
import com.aiflow.aimodel.factory.AiModelFactory;
import com.aiflow.aimodel.model.AiModelConfig;
import com.aiflow.aimodel.model.AiModelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * AI模型服务类
 * 提供AI模型的高级服务接口
 */
@Slf4j
@Service
public class AiModelService {
    
    private final AiModelFactory modelFactory;
    
    @Autowired
    public AiModelService(AiModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }
    
    /**
     * 生成文本
     */
    public String generateText(AiModelConfig config, String prompt) {
        log.info("使用模型 {} 生成文本", config.getName());
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.generateText(config, prompt);
    }
    
    /**
     * 异步生成文本
     */
    public CompletableFuture<String> generateTextAsync(AiModelConfig config, String prompt) {
        log.info("异步使用模型 {} 生成文本", config.getName());
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.generateTextAsync(config, prompt);
    }
    
    /**
     * 带参数生成文本
     */
    public String generateText(AiModelConfig config, String prompt, 
                             Integer maxTokens, Double temperature, Double topP) {
        log.info("使用模型 {} 生成文本，参数: maxTokens={}, temperature={}, topP={}", 
                config.getName(), maxTokens, temperature, topP);
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.generateText(config, prompt, maxTokens, temperature, topP);
    }
    
    /**
     * 带参数异步生成文本
     */
    public CompletableFuture<String> generateTextAsync(AiModelConfig config, String prompt,
                                                     Integer maxTokens, Double temperature, Double topP) {
        log.info("异步使用模型 {} 生成文本，参数: maxTokens={}, temperature={}, topP={}", 
                config.getName(), maxTokens, temperature, topP);
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.generateTextAsync(config, prompt, maxTokens, temperature, topP);
    }
    
    /**
     * 流式生成文本
     */
    public Stream<String> generateTextStream(AiModelConfig config, String prompt) {
        log.info("使用模型 {} 流式生成文本", config.getName());
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.generateTextStream(config, prompt);
    }
    
    /**
     * 带参数流式生成文本
     */
    public Stream<String> generateTextStream(AiModelConfig config, String prompt,
                                           Integer maxTokens, Double temperature, Double topP) {
        log.info("使用模型 {} 流式生成文本，参数: maxTokens={}, temperature={}, topP={}", 
                config.getName(), maxTokens, temperature, topP);
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.generateTextStream(config, prompt, maxTokens, temperature, topP);
    }
    
    /**
     * 测试模型连接
     */
    public CompletableFuture<String> testConnection(AiModelConfig config) {
        log.info("测试模型 {} 的连接", config.getName());
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.testConnection(config);
    }
    
    /**
     * 检查模型配置是否有效
     */
    public boolean isValidConfig(AiModelConfig config) {
        if (config == null) {
            return false;
        }
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.isValidConfig(config);
    }
    
    /**
     * 检查模型是否可用
     */
    public boolean isAvailable(AiModelConfig config) {
        if (config == null) {
            return false;
        }
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.isAvailable(config);
    }
    
    /**
     * 获取模型信息
     */
    public AiModelAdapter.ModelInfo getModelInfo(AiModelConfig config) {
        log.info("获取模型 {} 的信息", config.getName());
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.getModelInfo(config);
    }
    
    /**
     * 获取模型状态
     */
    public AiModelAdapter.ModelStatus getModelStatus(AiModelConfig config) {
        log.info("获取模型 {} 的状态", config.getName());
        AiModelAdapter adapter = modelFactory.getAdapter(config);
        return adapter.getModelStatus(config);
    }
    
    /**
     * 获取所有可用的模型类型
     */
    public List<AiModelType> getAvailableModelTypes() {
        return modelFactory.getAvailableModelTypes();
    }
    
    /**
     * 检查模型类型是否支持
     */
    public boolean isModelTypeSupported(AiModelType modelType) {
        return modelFactory.isModelTypeSupported(modelType);
    }
    
    /**
     * 检查模型类型是否支持
     */
    public boolean isModelTypeSupported(String modelTypeCode) {
        return modelFactory.isModelTypeSupported(modelTypeCode);
    }
} 