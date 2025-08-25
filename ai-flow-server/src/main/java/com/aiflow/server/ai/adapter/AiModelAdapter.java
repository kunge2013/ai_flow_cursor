package com.aiflow.server.ai.adapter;

import com.aiflow.server.dto.ModelDTO;
import com.aiflow.server.dto.ModelTestDTO;

/**
 * AI模型适配器接口
 */
public interface AiModelAdapter {

    /**
     * 测试模型连接
     *
     * @param testDTO 测试参数
     * @return 测试结果
     */
    String testConnection(ModelTestDTO testDTO);

    /**
     * 生成文本
     *
     * @param modelDTO 模型配置
     * @param prompt 提示词
     * @param maxTokens 最大token数
     * @param temperature 温度参数
     * @return 生成的文本
     */
    String generateText(ModelDTO modelDTO, String prompt, Integer maxTokens, Double temperature);

    /**
     * 获取模型类型
     *
     * @return 模型类型
     */
    String getModelType();

    /**
     * 检查模型配置是否有效
     *
     * @param modelDTO 模型配置
     * @return 是否有效
     */
    boolean isValidConfig(ModelDTO modelDTO);
} 