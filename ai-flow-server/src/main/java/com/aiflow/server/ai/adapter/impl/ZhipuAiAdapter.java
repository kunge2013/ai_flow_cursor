package com.aiflow.server.ai.adapter.impl;

import com.aiflow.server.ai.adapter.AiModelAdapter;
import com.aiflow.server.dto.ModelDTO;
import com.aiflow.server.dto.ModelTestDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.zhipu.ZhipuAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 智普AI模型适配器
 */
@Slf4j
@Component
public class ZhipuAiAdapter implements AiModelAdapter {

    @Override
    public String testConnection(ModelTestDTO testDTO) {
        try {
            ChatLanguageModel model = ZhipuAiChatModel.builder()
                    .apiKey(testDTO.getApiKey())
                    .baseUrl(testDTO.getApiEndpoint())
                    .maxTokens(testDTO.getMaxTokens())
                    .temperature(testDTO.getTemperature())
                    .build();

            String response = model.generate("Hello, this is a test message.");
            log.info("智普AI连接测试成功: {}", response);
            return "连接成功！测试响应: " + response;
        } catch (Exception e) {
            log.error("智普AI连接测试失败", e);
            return "连接失败: " + e.getMessage();
        }
    }

    @Override
    public String generateText(ModelDTO modelDTO, String prompt, Integer maxTokens, Double temperature) {
        try {
            ChatLanguageModel model = ZhipuAiChatModel.builder()
                    .apiKey(modelDTO.getApiKey())
                    .baseUrl(modelDTO.getApiEndpoint())
                    .modelName(modelDTO.getBaseModel())
                    .maxTokens(maxTokens != null ? maxTokens : modelDTO.getMaxTokens())
                    .temperature(temperature != null ? temperature : modelDTO.getTemperature())
                    .build();

            String response = model.generate(prompt);
            log.info("智普AI文本生成成功: {}", response);
            return response;
        } catch (Exception e) {
            log.error("智普AI文本生成失败", e);
            throw new RuntimeException("智普AI文本生成失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getModelType() {
        return "zhipu";
    }

    @Override
    public boolean isValidConfig(ModelDTO modelDTO) {
        return modelDTO != null 
                && modelDTO.getApiKey() != null && !modelDTO.getApiKey().trim().isEmpty()
                && modelDTO.getApiEndpoint() != null && !modelDTO.getApiEndpoint().trim().isEmpty()
                && modelDTO.getBaseModel() != null && !modelDTO.getBaseModel().trim().isEmpty();
    }
} 