package com.aiflow.server.service;

import com.aiflow.server.dto.ModelDTO;
import com.aiflow.server.dto.ModelQueryDTO;
import com.aiflow.server.dto.ModelTestDTO;
import com.aiflow.server.entity.Model;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 模型服务接口
 */
public interface ModelService extends IService<Model> {

    /**
     * 分页查询模型列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<Model> getModelPage(ModelQueryDTO queryDTO);

    /**
     * 创建模型
     *
     * @param modelDTO 模型信息
     * @return 是否成功
     */
    boolean createModel(ModelDTO modelDTO);

    /**
     * 更新模型
     *
     * @param modelDTO 模型信息
     * @return 是否成功
     */
    boolean updateModel(ModelDTO modelDTO);

    /**
     * 删除模型
     *
     * @param id 模型ID
     * @return 是否成功
     */
    boolean deleteModel(Long id);

    /**
     * 测试模型接口
     *
     * @param testDTO 测试参数
     * @return 测试结果
     */
    String testModel(ModelTestDTO testDTO);

    /**
     * 根据ID获取模型详情
     *
     * @param id 模型ID
     * @return 模型信息
     */
    ModelDTO getModelById(Long id);
} 