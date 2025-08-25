-- 向量存储相关表结构

drop TABLE t_knowledge_base;
-- 知识库表
CREATE TABLE IF NOT EXISTS t_knowledge_base (
    id VARCHAR(128) PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '知识库名称',
    description TEXT COMMENT '知识库描述',
    vector_model VARCHAR(50) NOT NULL DEFAULT 'openai' COMMENT '向量模型',
    status BOOLEAN NOT NULL DEFAULT TRUE COMMENT '状态：true-启用，false-禁用',
    tags TEXT COMMENT '标签，JSON格式存储',
    config_json TEXT COMMENT '配置信息，JSON格式存储',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除'
);

-- 向量文档表
CREATE TABLE IF NOT EXISTS t_vector_document (
    id VARCHAR(128) PRIMARY KEY,
    kb_id VARCHAR(32) NOT NULL COMMENT '知识库ID',
    title VARCHAR(200) NOT NULL COMMENT '文档标题',
    content LONGTEXT NOT NULL COMMENT '文档内容',
    content_type VARCHAR(50) COMMENT '内容类型',
    file_size BIGINT COMMENT '文件大小（字节）',
    file_path VARCHAR(500) COMMENT '文件路径',
    vector_model VARCHAR(50) NOT NULL COMMENT '向量模型',
    embedding LONGTEXT COMMENT '向量数据，JSON格式存储',
    metadata TEXT COMMENT '元数据，JSON格式存储',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待处理，processing-处理中，completed-已完成，failed-失败',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_kb_id (kb_id),
    INDEX idx_status (status),
    INDEX idx_vector_model (vector_model)
);

-- 向量索引表（用于快速检索）
CREATE TABLE IF NOT EXISTS t_vector_index (
    id VARCHAR(128) PRIMARY KEY,
    document_id VARCHAR(32) NOT NULL COMMENT '文档ID',
    kb_id VARCHAR(32) NOT NULL COMMENT '知识库ID',
    vector_model VARCHAR(50) NOT NULL COMMENT '向量模型',
    embedding_vector LONGTEXT NOT NULL COMMENT '向量数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_document_id (document_id),
    INDEX idx_kb_id (kb_id),
    INDEX idx_vector_model (vector_model)
);

-- 向量检索日志表
CREATE TABLE IF NOT EXISTS t_vector_search_log (
    id VARCHAR(128) PRIMARY KEY,
    kb_id VARCHAR(128) NOT NULL COMMENT '知识库ID',
    query TEXT NOT NULL COMMENT '查询内容',
    query_embedding LONGTEXT COMMENT '查询向量',
    top_k INT NOT NULL DEFAULT 5 COMMENT '返回结果数量',
    score_threshold DECIMAL(3,2) NOT NULL DEFAULT 0.70 COMMENT '相似度阈值',
    result_count INT NOT NULL DEFAULT 0 COMMENT '结果数量',
    search_time BIGINT COMMENT '搜索耗时（毫秒）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_kb_id (kb_id),
    INDEX idx_created_at (created_at)
);

-- 插入示例数据
INSERT INTO t_knowledge_base (id, name, description, vector_model, status, tags) VALUES
('kb_001', 'AI技术文档库', '包含人工智能相关的技术文档和教程', 'openai', TRUE, '["AI", "技术", "教程"]'),
('kb_002', '产品手册库', '产品使用说明和操作手册', 'zhipu', TRUE, '["产品", "手册", "说明"]'),
('kb_003', '知识问答库', '常见问题和解答', 'deepseek', TRUE, '["问答", "FAQ", "帮助"]');

-- 插入示例文档
INSERT INTO t_vector_document (id, kb_id, title, content, content_type, file_size, vector_model, status) VALUES
('doc_001', 'kb_001', '机器学习基础', '机器学习是人工智能的一个重要分支，它使计算机能够在没有明确编程的情况下学习和改进。本文档介绍了机器学习的基本概念、算法和应用。', 'text', 1024, 'openai', 'completed'),
('doc_002', 'kb_001', '深度学习入门', '深度学习是机器学习的一个子集，它使用多层神经网络来模拟人脑的学习过程。本文档详细介绍了深度学习的原理和实践。', 'text', 2048, 'openai', 'completed'),
('doc_003', 'kb_002', '产品安装指南', '本指南详细说明了如何安装和配置我们的产品。包括系统要求、安装步骤和常见问题解决方案。', 'text', 1536, 'zhipu', 'completed'); 