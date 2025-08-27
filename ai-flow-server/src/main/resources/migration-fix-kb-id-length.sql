-- 数据库迁移脚本：修复kb_id列长度不足问题
-- 执行日期：2025-08-27
-- 问题：kb_id列定义为VARCHAR(32)，但UUID长度为36字符，导致数据截断错误

-- 1. 修改向量文档表的kb_id列长度
ALTER TABLE t_vector_document MODIFY COLUMN kb_id VARCHAR(128) NOT NULL COMMENT '知识库ID';

-- 2. 修改向量索引表的document_id和kb_id列长度
ALTER TABLE t_vector_index MODIFY COLUMN document_id VARCHAR(128) NOT NULL COMMENT '文档ID';
ALTER TABLE t_vector_index MODIFY COLUMN kb_id VARCHAR(128) NOT NULL COMMENT '知识库ID';

-- 3. 修改向量检索日志表的kb_id列长度
ALTER TABLE t_vector_search_log MODIFY COLUMN kb_id VARCHAR(128) NOT NULL COMMENT '知识库ID';

-- 4. 验证修改结果
-- 查看表结构确认修改成功
-- DESCRIBE t_vector_document;
-- DESCRIBE t_vector_index;
-- DESCRIBE t_vector_search_log;

-- 注意：执行此脚本前请先备份数据库
-- 如果表中已有数据，建议在业务低峰期执行
