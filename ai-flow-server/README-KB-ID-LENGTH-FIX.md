# 知识库ID列长度问题修复说明

## 问题描述

**错误信息：**
```
Data truncation: Data too long for column 'kb_id' at row 1
```

**问题原因：**
数据库表 `t_vector_document` 中的 `kb_id` 列定义为 `VARCHAR(32)`，但系统使用 `IdService.newId()` 生成 UUID 字符串，长度为 36 个字符（包含连字符），超出了数据库列的长度限制。

## 影响范围

- **t_vector_document** 表：kb_id 列
- **t_vector_index** 表：document_id 和 kb_id 列  
- **t_vector_search_log** 表：kb_id 列

## 解决方案

### 方案1：修改数据库表结构（推荐）

**优点：**
- 保持与主键 `id` 列的一致性
- UUID 是标准的唯一标识符，更可靠
- 128 字符长度足够存储各种 ID 格式

**执行步骤：**
1. 备份数据库
2. 执行迁移脚本：`migration-fix-kb-id-length.sql`
3. 重启应用服务

### 方案2：修改ID生成策略

**优点：**
- 不需要修改数据库结构
- 向后兼容

**缺点：**
- 需要修改代码逻辑
- 可能影响现有功能

## 修复后的表结构

```sql
-- 向量文档表
CREATE TABLE IF NOT EXISTS t_vector_document (
    id VARCHAR(128) PRIMARY KEY,
    kb_id VARCHAR(128) NOT NULL COMMENT '知识库ID',  -- 从32改为128
    -- ... 其他字段
);

-- 向量索引表
CREATE TABLE IF NOT EXISTS t_vector_index (
    id VARCHAR(128) PRIMARY KEY,
    document_id VARCHAR(128) NOT NULL COMMENT '文档ID',  -- 从32改为128
    kb_id VARCHAR(128) NOT NULL COMMENT '知识库ID',      -- 从32改为128
    -- ... 其他字段
);

-- 向量检索日志表
CREATE TABLE IF NOT EXISTS t_vector_search_log (
    id VARCHAR(128) PRIMARY KEY,
    kb_id VARCHAR(128) NOT NULL COMMENT '知识库ID',  -- 从32改为128
    -- ... 其他字段
);
```

## 执行迁移

### 方法1：使用迁移脚本
```bash
# 连接到MySQL数据库
mysql -u username -p database_name

# 执行迁移脚本
source migration-fix-kb-id-length.sql;
```

### 方法2：手动执行SQL
```sql
-- 修改向量文档表
ALTER TABLE t_vector_document MODIFY COLUMN kb_id VARCHAR(128) NOT NULL COMMENT '知识库ID';

-- 修改向量索引表
ALTER TABLE t_vector_index MODIFY COLUMN document_id VARCHAR(128) NOT NULL COMMENT '文档ID';
ALTER TABLE t_vector_index MODIFY COLUMN kb_id VARCHAR(128) NOT NULL COMMENT '知识库ID';

-- 修改向量检索日志表
ALTER TABLE t_vector_search_log MODIFY COLUMN kb_id VARCHAR(128) NOT NULL COMMENT '知识库ID';
```

## 验证修复

执行以下SQL验证修改是否成功：

```sql
-- 查看表结构
DESCRIBE t_vector_document;
DESCRIBE t_vector_index;
DESCRIBE t_vector_search_log;

-- 确认kb_id列长度为128
SHOW CREATE TABLE t_vector_document;
```

## 注意事项

1. **备份数据**：执行迁移前务必备份数据库
2. **业务影响**：建议在业务低峰期执行
3. **应用重启**：修改表结构后需要重启应用服务
4. **测试验证**：修复后测试相关功能是否正常

## 预防措施

1. **设计规范**：ID字段长度应统一为128字符
2. **代码审查**：新增表结构时检查ID字段长度
3. **测试覆盖**：增加数据库字段长度相关的测试用例

## 相关文件

- `schema-vector.sql` - 修复后的表结构定义
- `migration-fix-kb-id-length.sql` - 数据库迁移脚本
- `IdService.java` - ID生成服务
- `VectorDocument.java` - 向量文档实体类
