package com.aiflow.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 模型实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_model")
public class Model {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 基础模型
     */
    @TableField("base_model")
    private String baseModel;

    /**
     * 模型类型
     */
    @TableField("model_type")
    private String modelType;

    /**
     * 状态：active-启用，inactive-禁用
     */
    @TableField("status")
    private String status;

    /**
     * API地址
     */
    @TableField("api_endpoint")
    private String apiEndpoint;

    /**
     * API密钥
     */
    @TableField("api_key")
    private String apiKey;

    /**
     * 模型描述
     */
    @TableField("description")
    private String description;

    /**
     * 最大Token数
     */
    @TableField("max_tokens")
    private Integer maxTokens;

    /**
     * 温度参数
     */
    @TableField("temperature")
    private Double temperature;

    /**
     * Top P参数
     */
    @TableField("top_p")
    private Double topP;

    /**
     * 频率惩罚
     */
    @TableField("frequency_penalty")
    private Double frequencyPenalty;

    /**
     * 存在惩罚
     */
    @TableField("presence_penalty")
    private Double presencePenalty;

    /**
     * 停止词，JSON格式
     */
    @TableField("stop_words")
    private String stopWords;

    /**
     * 其他配置参数JSON
     */
    @TableField("config_json")
    private String configJson;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
} 