package com.aiflow.server.dto;

import lombok.Data;

/**
 * 模型查询DTO类
 */
@Data
public class ModelQueryDTO {

    /**
     * 模型名称（模糊查询）
     */
    private String modelName;

    /**
     * 模型类型
     */
    private String modelType;

    /**
     * 基础模型
     */
    private String baseModel;

    /**
     * 状态
     */
    private String status;

    /**
     * 当前页码
     */
    private Integer currentPage = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
} 