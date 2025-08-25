package com.aiflow.aimodel.model;

/**
 * AI模型类型枚举
 */
public enum AiModelType {
    
    /**
     * OpenAI
     */
    OPENAI("openai", "OpenAI", "OpenAI GPT系列模型"),
    
    /**
     * Anthropic Claude
     */
    ANTHROPIC("anthropic", "Anthropic Claude", "Anthropic Claude系列模型"),
    
    /**
     * Google Gemini
     */
    GOOGLE_GEMINI("google-gemini", "Google Gemini", "Google Gemini系列模型"),
    
    /**
     * Ollama
     */
    OLLAMA("ollama", "Ollama", "本地Ollama模型"),
    
    /**
     * 智普AI
     */
    ZHIPU_AI("zhipu-ai", "智普AI", "智普AI GLM系列模型"),
    
    /**
     * 通义千问
     */
    QIANFAN("qianfan", "通义千问", "阿里通义千问系列模型"),
    
    /**
     * Hugging Face
     */
    HUGGING_FACE("hugging-face", "Hugging Face", "Hugging Face开源模型"),
    
    /**
     * 自定义模型
     */
    CUSTOM("custom", "自定义模型", "自定义配置的模型");

    private final String code;
    private final String name;
    private final String description;

    AiModelType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static AiModelType fromCode(String code) {
        for (AiModelType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return CUSTOM;
    }
    
    public static AiModelType fromName(String name) {
        for (AiModelType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return CUSTOM;
    }
} 