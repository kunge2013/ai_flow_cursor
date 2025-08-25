package com.aiflow.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("t_knowledge_base")
public class KnowledgeBase {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("name")
    private String name;
    
    @TableField("description")
    private String description;
    
    @TableField("vector_model")
    private String vectorModel;
    
    @TableField("status")
    private Boolean status;
    
    @TableField("tags")
    private String tags; // JSON格式存储标签数组
    
    @TableField("config_json")
    private String configJson; // JSON格式存储配置信息
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
    
    // 非数据库字段
    @TableField(exist = false)
    private List<String> tagList;
    
    @TableField(exist = false)
    private Long documentCount;
} 