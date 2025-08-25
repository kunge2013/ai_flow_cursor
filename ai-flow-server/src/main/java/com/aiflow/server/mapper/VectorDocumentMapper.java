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
} 