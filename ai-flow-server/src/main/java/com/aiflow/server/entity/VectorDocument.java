package com.aiflow.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_vector_document")
public class VectorDocument {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("kb_id")
    private String kbId;
    
    @TableField("title")
    private String title;
    
    @TableField("content")
    private String content;
    
    @TableField("content_type")
    private String contentType;
    
    @TableField("file_size")
    private Long fileSize;
    
    @TableField("file_path")
    private String filePath;
    
    @TableField("vector_model")
    private String vectorModel;
    
    @TableField("embedding")
    private String embedding; // JSON格式存储向量数据
    
    @TableField("metadata")
    private String metadata; // JSON格式存储元数据
    
    @TableField("status")
    private String status; // pending, processing, completed, failed
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
} 