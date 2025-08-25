package com.aiflow.aimodel.adapter.impl;

import com.aiflow.aimodel.adapter.AiModelAdapter;
import com.aiflow.aimodel.model.AiModelConfig;
import com.aiflow.aimodel.model.AiModelType;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * OpenAI模型适配器
 * 基于LangChain4j实现
 */
@Slf4j
@Component
public class OpenAiAdapter implements AiModelAdapter {
    
    @Override
    public AiModelType getSupportedType() {
        return AiModelType.OPENAI;
    }
    
    @Override
    public boolean isValidConfig(AiModelConfig config) {
        return config != null 
                && config.getType() == AiModelType.OPENAI
                && config.getApiKey() != null && !config.getApiKey().trim().isEmpty()
                && config.getBaseModel() != null && !config.getBaseModel().trim().isEmpty();
    }
    
    @Override
    public CompletableFuture<String> testConnection(AiModelConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ChatLanguageModel model = createModel(config);
                String response = model.generate("Hello, this is a test message.");
                log.info("OpenAI连接测试成功: {}", response);
                return "连接成功！测试响应: " + response;
            } catch (Exception e) {
                log.error("OpenAI连接测试失败", e);
                return "连接失败: " + e.getMessage();
            }
        });
    }
    
    @Override
    public String generateText(AiModelConfig config, String prompt) {
        try {
            ChatLanguageModel model = createModel(config);
            return model.generate(prompt);
        } catch (Exception e) {
            log.error("OpenAI文本生成失败", e);
            throw new RuntimeException("OpenAI文本生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CompletableFuture<String> generateTextAsync(AiModelConfig config, String prompt) {
        return CompletableFuture.supplyAsync(() -> generateText(config, prompt));
    }
    
    @Override
    public String generateText(AiModelConfig config, String prompt, 
                             Integer maxTokens, Double temperature, Double topP) {
        try {
            ChatLanguageModel model = createModelWithParams(config, maxTokens, temperature, topP);
            return model.generate(prompt);
        } catch (Exception e) {
            log.error("OpenAI文本生成失败", e);
            throw new RuntimeException("OpenAI文本生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CompletableFuture<String> generateTextAsync(AiModelConfig config, String prompt,
                                                     Integer maxTokens, Double temperature, Double topP) {
        return CompletableFuture.supplyAsync(() -> 
            generateText(config, prompt, maxTokens, temperature, topP));
    }
    
    @Override
    public Stream<String> generateTextStream(AiModelConfig config, String prompt) {
        try {
            ChatLanguageModel model = createModel(config);
            // 注意：这里需要根据实际的流式API实现
            // 目前LangChain4j的流式支持可能有限
            String response = model.generate(prompt);
            return Stream.of(response);
        } catch (Exception e) {
            log.error("OpenAI流式文本生成失败", e);
            throw new RuntimeException("OpenAI流式文本生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Stream<String> generateTextStream(AiModelConfig config, String prompt,
                                          Integer maxTokens, Double temperature, Double topP) {
        try {
            ChatLanguageModel model = createModelWithParams(config, maxTokens, temperature, topP);
            String response = model.generate(prompt);
            return Stream.of(response);
        } catch (Exception e) {
            log.error("OpenAI流式文本生成失败", e);
            throw new RuntimeException("OpenAI流式文本生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ModelInfo getModelInfo(AiModelConfig config) {
        return new ModelInfo(
            config.getBaseModel(),
            config.getName(),
            "OpenAI",
            config.getMaxTokens().longValue(),
            new String[]{"text-generation", "chat", "streaming"},
            LocalDateTime.now()
        );
    }
    
    @Override
    public boolean isAvailable(AiModelConfig config) {
        try {
            return testConnection(config).get() != null;
        } catch (Exception e) {
            log.error("检查OpenAI模型可用性失败", e);
            return false;
        }
    }
    
    @Override
    public ModelStatus getModelStatus(AiModelConfig config) {
        if (!isValidConfig(config)) {
            return ModelStatus.ERROR;
        }
        
        try {
            if (isAvailable(config)) {
                return ModelStatus.AVAILABLE;
            } else {
                return ModelStatus.UNAVAILABLE;
            }
        } catch (Exception e) {
            return ModelStatus.ERROR;
        }
    }
    
    /**
     * 创建OpenAI模型实例
     */
    private ChatLanguageModel createModel(AiModelConfig config) {
        return OpenAiChatModel.builder()
                .apiKey(config.getApiKey())
                .baseUrl(config.getApiEndpoint() != null ? config.getApiEndpoint() : "https://api.openai.com")
                .modelName(config.getBaseModel())
                .maxTokens(config.getMaxTokens())
                .temperature(config.getTemperature())
                .topP(config.getTopP())
                .timeout(Duration.ofMillis(config.getTimeout()))
                .maxRetries(config.getMaxRetries())
                .build();
    }
    
    /**
     * 创建带参数的OpenAI模型实例
     */
    private ChatLanguageModel createModelWithParams(AiModelConfig config, 
                                                  Integer maxTokens, Double temperature, Double topP) {
        return OpenAiChatModel.builder()
                .apiKey(config.getApiKey())
                .baseUrl(config.getApiEndpoint() != null ? config.getApiEndpoint() : "https://api.openai.com")
                .modelName(config.getBaseModel())
                .maxTokens(maxTokens != null ? maxTokens : config.getMaxTokens())
                .temperature(temperature != null ? temperature : config.getTemperature())
                .topP(topP != null ? topP : config.getTopP())
                .timeout(Duration.ofMillis(config.getTimeout()))
                .maxRetries(config.getMaxRetries())
                .build();
    }
} 