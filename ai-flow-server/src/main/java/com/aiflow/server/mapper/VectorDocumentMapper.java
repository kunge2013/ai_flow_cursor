package com.aiflow.server.mapper;

import com.aiflow.server.entity.VectorDocument;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 向量文档 Mapper 接口
 */
@Mapper
public interface VectorDocumentMapper extends BaseMapper<VectorDocument> {

    /**
     * 根据知识库ID查询文档列表
     *
     * @param kbId 知识库ID
     * @return 文档列表
     */
    List<VectorDocument> selectByKbId(@Param("kbId") String kbId);

    /**
     * 根据知识库ID删除所有文档
     *
     * @param kbId 知识库ID
     * @return 删除的记录数
     */
    int deleteByKbId(@Param("kbId") String kbId);

    /**
     * 根据知识库ID统计文档数量
     *
     * @param kbId 知识库ID
     * @return 文档数量
     */
    Long countByKbId(@Param("kbId") String kbId);

    /**
     * 根据知识库ID和状态统计文档数量
     *
     * @param kbId 知识库ID
     * @param status 状态
     * @return 文档数量
     */
    Long countByKbIdAndStatus(@Param("kbId") String kbId, @Param("status") String status);

    /**
     * 根据知识库ID和标题查询文档
     *
     * @param kbId 知识库ID
     * @param title 标题
     * @return 文档
     */
    VectorDocument selectByKbIdAndTitle(@Param("kbId") String kbId, @Param("title") String title);
} 