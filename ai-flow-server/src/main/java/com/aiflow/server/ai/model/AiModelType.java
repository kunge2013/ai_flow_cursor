package com.aiflow.server.ai.model;

/**
 * AI模型类型枚举
 */
public enum AiModelType {
    
    /**
     * 智普AI
     */
    ZHIPU_AI("zhipu", "智普AI"),
    
    /**
     * DeepSeek
     */
    DEEPSEEK("deepseek", "DeepSeek"),
    
    /**
     * OpenAI
     */
    OPENAI("openai", "OpenAI"),
    
    /**
     * Claude
     */
    CLAUDE("claude", "Claude"),
    
    /**
     * Gemini
     */
    GEMINI("gemini", "Gemini"),
    
    /**
     * 通义千问
     */
    QIANFAN("qianfan", "通义千问"),
    
    /**
     * 自定义模型
     */
    CUSTOM("custom", "自定义模型");

    private final String code;
    private final String name;

    AiModelType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static AiModelType fromCode(String code) {
        for (AiModelType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return CUSTOM;
    }
} 