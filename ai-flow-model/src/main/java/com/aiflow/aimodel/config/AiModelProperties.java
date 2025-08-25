package com.aiflow.aimodel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AI模型配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai.model")
public class AiModelProperties {
    
    /**
     * 默认模型配置
     */
    private DefaultModel defaultModel = new DefaultModel();
    
    /**
     * OpenAI配置
     */
    private OpenAi openai = new OpenAi();
    
    /**
     * Anthropic配置
     */
    private Anthropic anthropic = new Anthropic();
    
    /**
     * Google Gemini配置
     */
    private GoogleGemini googleGemini = new GoogleGemini();
    
    /**
     * Ollama配置
     */
    private Ollama ollama = new Ollama();
    
    /**
     * 智普AI配置
     */
    private ZhipuAi zhipuAi = new ZhipuAi();
    
    /**
     * 通义千问配置
     */
    private Qianfan qianfan = new Qianfan();
    
    @Data
    public static class DefaultModel {
        private String type = "openai";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class OpenAi {
        private String apiKey;
        private String baseUrl = "https://api.openai.com";
        private String model = "gpt-3.5-turbo";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class Anthropic {
        private String apiKey;
        private String baseUrl = "https://api.anthropic.com";
        private String model = "claude-3-sonnet-20240229";
        private Integer maxTokens = 4096;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class GoogleGemini {
        private String apiKey;
        private String baseUrl = "https://generativelanguage.googleapis.com";
        private String model = "gemini-pro";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class Ollama {
        private String baseUrl = "http://localhost:11434";
        private String model = "llama2";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class ZhipuAi {
        private String apiKey;
        private String baseUrl = "https://open.bigmodel.cn";
        private String model = "glm-4";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class Qianfan {
        private String apiKey;
        private String secretKey;
        private String baseUrl = "https://aip.baidubce.com";
        private String model = "qwen-turbo";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
} 