package com.aiflow.server.mapper;

import com.aiflow.server.entity.Model;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 模型Mapper接口
 */
@Mapper
public interface ModelMapper extends BaseMapper<Model> {

    /**
     * 分页查询模型列表
     *
     * @param page 分页参数
     * @param modelName 模型名称（模糊查询）
     * @param modelType 模型类型
     * @param baseModel 基础模型
     * @param status 状态
     * @return 分页结果
     */
    IPage<Model> selectModelPage(Page<Model> page,
                                @Param("modelName") String modelName,
                                @Param("modelType") String modelType,
                                @Param("baseModel") String baseModel,
                                @Param("status") String status);
} 