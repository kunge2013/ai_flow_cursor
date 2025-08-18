-- AI Flow Database Schema
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_flow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ai_flow;

-- 流程表
CREATE TABLE IF NOT EXISTS t_flow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flow_id VARCHAR(255) NOT NULL UNIQUE COMMENT '流程ID',
    name VARCHAR(255) NOT NULL COMMENT '流程名称',
    description TEXT COMMENT '流程描述',
    graph_json LONGTEXT COMMENT '流程图JSON',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_flow_id (flow_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程表';

-- 应用表
CREATE TABLE IF NOT EXISTS t_app (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    app_id VARCHAR(255) NOT NULL UNIQUE COMMENT '应用ID',
    name VARCHAR(255) NOT NULL COMMENT '应用名称',
    description TEXT COMMENT '应用描述',
    flow_id VARCHAR(255) COMMENT '关联流程ID',
    config_json LONGTEXT COMMENT '应用配置JSON',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_app_id (app_id),
    INDEX idx_flow_id (flow_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用表';

-- 节点类型表
CREATE TABLE IF NOT EXISTS t_node_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_code VARCHAR(100) NOT NULL UNIQUE COMMENT '节点类型代码',
    type_name VARCHAR(255) NOT NULL COMMENT '节点类型名称',
    description TEXT COMMENT '节点描述',
    icon VARCHAR(255) COMMENT '节点图标',
    category VARCHAR(100) COMMENT '节点分类',
    config_schema LONGTEXT COMMENT '配置模式JSON Schema',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_type_code (type_code),
    INDEX idx_category (category),
    INDEX idx_enabled (enabled),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节点类型表';

-- 初始化节点类型数据
INSERT INTO t_node_type (type_code, type_name, description, icon, category, enabled) VALUES
('start', '开始节点', '流程开始节点', 'start', 'control', TRUE),
('end', '结束节点', '流程结束节点', 'stop', 'control', TRUE),
('llm', 'LLM节点', '大语言模型处理节点', 'robot', 'ai', TRUE),
('classifier', '分类器', '文本分类节点', 'filter', 'ai', TRUE),
('knowledge_base', '知识库', '知识库查询节点', 'database', 'data', TRUE),
('condition', '条件分支', '条件判断分支节点', 'fork', 'control', TRUE),
('script', '脚本执行', '脚本代码执行节点', 'code', 'logic', TRUE),
('java_enhance', 'Java增强', 'Java代码增强节点', 'coffee', 'logic', TRUE),
('http_request', 'HTTP请求', 'HTTP接口请求节点', 'link', 'integration', TRUE),
('subprocess', '子流程', '调用子流程节点', 'sitemap', 'control', TRUE),
('direct_reply', '直接回复', '直接输出回复节点', 'message', 'output', TRUE)
ON DUPLICATE KEY UPDATE type_name=VALUES(type_name), description=VALUES(description); 



-- Migration to fix config_json column size
USE ai_flow;

-- Modify config_json column from TEXT to LONGTEXT to accommodate larger JSON data
ALTER TABLE t_app MODIFY COLUMN config_json LONGTEXT COMMENT '应用配置JSON'; 