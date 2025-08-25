package com.aiflow.server.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AI模型配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai.model")
public class AiModelConfig {

    /**
     * 智普AI配置
     */
    private ZhipuAiConfig zhipu = new ZhipuAiConfig();

    /**
     * DeepSeek配置
     */
    private DeepSeekConfig deepseek = new DeepSeekConfig();

    /**
     * OpenAI配置
     */
    private OpenAiConfig openai = new OpenAiConfig();

    /**
     * 智普AI配置
     */
    @Data
    public static class ZhipuAiConfig {
        private String apiKey;
        private String baseUrl = "https://open.bigmodel.cn/api/paas/v4";
        private String defaultModel = "glm-4";
        private Integer maxTokens = 4096;
        private Double temperature = 0.7;
    }

    /**
     * DeepSeek配置
     */
    @Data
    public static class DeepSeekConfig {
        private String apiKey;
        private String baseUrl = "https://api.deepseek.com";
        private String defaultModel = "deepseek-chat";
        private Integer maxTokens = 4096;
        private Double temperature = 0.7;
    }

    /**
     * OpenAI配置
     */
    @Data
    public static class OpenAiConfig {
        private String apiKey;
        private String baseUrl = "https://api.openai.com";
        private String defaultModel = "gpt-3.5-turbo";
        private Integer maxTokens = 4096;
        private Double temperature = 0.7;
    }
} 