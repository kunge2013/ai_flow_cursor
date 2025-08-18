package com.aiflow.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_node_type")
public class NodeTypeEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("type_code")
    private String typeCode;
    
    @TableField("type_name")
    private String typeName;
    
    @TableField("description")
    private String description;
    
    @TableField("icon")
    private String icon;
    
    @TableField("category")
    private String category;
    
    @TableField("config_schema")
    private String configSchema;
    
    @TableField("enabled")
    private Boolean enabled;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
} 