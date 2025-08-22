package com.aiflow.server.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 模型DTO类
 */
@Data
public class ModelDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空")
    @Size(min = 2, max = 50, message = "模型名称长度必须在2-50个字符之间")
    private String modelName;

    /**
     * 基础模型
     */
    @NotBlank(message = "基础模型不能为空")
    private String baseModel;

    /**
     * 模型类型
     */
    @NotBlank(message = "模型类型不能为空")
    private String modelType;

    /**
     * 状态：active-启用，inactive-禁用
     */
    private String status = "active";

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
     * 模型描述
     */
    @Size(max = 500, message = "模型描述不能超过500个字符")
    private String description;

    /**
     * 最大Token数
     */
    @NotNull(message = "最大Token数不能为空")
    private Integer maxTokens = 4096;

    /**
     * 温度参数
     */
    private Double temperature = 0.7;

    /**
     * Top P参数
     */
    private Double topP = 1.0;

    /**
     * 频率惩罚
     */
    private Double frequencyPenalty = 0.0;

    /**
     * 存在惩罚
     */
    private Double presencePenalty = 0.0;

    /**
     * 停止词，JSON格式
     */
    private String stopWords;

    /**
     * 其他配置参数JSON
     */
    private String configJson;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 