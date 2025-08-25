package com.aiflow.aimodel.adapter;

import com.aiflow.aimodel.model.AiModelConfig;
import com.aiflow.aimodel.model.AiModelType;

import java.util.concurrent.CompletableFuture;

/**
 * AI模型适配器接口
 * 基于LangChain4j实现
 */
public interface AiModelAdapter {
    
    /**
     * 获取支持的模型类型
     */
    AiModelType getSupportedType();
    
    /**
     * 检查配置是否有效
     */
    boolean isValidConfig(AiModelConfig config);
    
    /**
     * 测试模型连接
     */
    CompletableFuture<String> testConnection(AiModelConfig config);
    
    /**
     * 生成文本（同步）
     */
    String generateText(AiModelConfig config, String prompt);
    
    /**
     * 生成文本（异步）
     */
    CompletableFuture<String> generateTextAsync(AiModelConfig config, String prompt);
    
    /**
     * 生成文本（带参数）
     */
    String generateText(AiModelConfig config, String prompt, 
                       Integer maxTokens, Double temperature, Double topP);
    
    /**
     * 生成文本（带参数，异步）
     */
    CompletableFuture<String> generateTextAsync(AiModelConfig config, String prompt,
                                              Integer maxTokens, Double temperature, Double topP);
    
    /**
     * 流式生成文本
     */
    java.util.stream.Stream<String> generateTextStream(AiModelConfig config, String prompt);
    
    /**
     * 流式生成文本（带参数）
     */
    java.util.stream.Stream<String> generateTextStream(AiModelConfig config, String prompt,
                                                     Integer maxTokens, Double temperature, Double topP);
    
    /**
     * 获取模型信息
     */
    ModelInfo getModelInfo(AiModelConfig config);
    
    /**
     * 检查模型是否可用
     */
    boolean isAvailable(AiModelConfig config);
    
    /**
     * 获取模型状态
     */
    ModelStatus getModelStatus(AiModelConfig config);
    
    /**
     * 模型信息
     */
    record ModelInfo(
        String modelId,
        String modelName,
        String provider,
        Long contextLength,
        String[] supportedFeatures,
        java.time.LocalDateTime lastUpdated
    ) {}
    
    /**
     * 模型状态
     */
    enum ModelStatus {
        AVAILABLE,      // 可用
        UNAVAILABLE,    // 不可用
        LOADING,        // 加载中
        ERROR,          // 错误
        UNKNOWN         // 未知
    }
} 