package com.aiflow.server.mapper;

import com.aiflow.server.entity.KnowledgeBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 知识库 Mapper 接口
 */
@Mapper
public interface KnowledgeBaseMapper extends BaseMapper<KnowledgeBase> {

    /**
     * 分页查询知识库列表
     *
     * @param page 分页参数
     * @param name 名称（模糊查询）
     * @param vectorModel 向量模型
     * @param status 状态
     * @return 分页结果
     */
    IPage<KnowledgeBase> selectKnowledgeBasePage(Page<KnowledgeBase> page,
                                                 @Param("name") String name,
                                                 @Param("vectorModel") String vectorModel,
                                                 @Param("status") Boolean status);
} 