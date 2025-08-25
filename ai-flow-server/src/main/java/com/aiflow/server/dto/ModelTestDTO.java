package com.aiflow.server.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 模型测试DTO类
 */
@Data
public class ModelTestDTO {

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * API地址
     */
    @NotBlank(message = "API地址不能为空")
    private String apiEndpoint;

    /**
     * API密钥
     */
    @NotBlank(message = "API密钥不能为空")
    private String apiKey;

    /**
     * 测试输入内容
     */
    private String testInput = "Hello, this is a test message.";

    /**
     * 最大Token数
     */
    private Integer maxTokens = 100;

    /**
     * 温度参数
     */
    private Double temperature = 0.7;
} 