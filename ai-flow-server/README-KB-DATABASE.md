# 知识库数据库持久化实现

## 概述

本项目已完成知识库模块从内存存储到MySQL数据库的迁移，实现了真正的数据持久化存储。

## 主要改进

### 1. 数据库持久化
- ✅ 使用MyBatis-Plus替代内存存储(ConcurrentHashMap)
- ✅ 支持MySQL数据库自动建表
- ✅ 实现了完整的CRUD操作
- ✅ 添加了逻辑删除支持
- ✅ 支持事务管理

### 2. 新增组件

#### Mapper接口
- `KnowledgeBaseMapper`: 知识库数据访问接口
- `VectorDocumentMapper`: 向量文档数据访问接口

#### XML映射文件
- `KnowledgeBaseMapper.xml`: 知识库SQL映射
- `VectorDocumentMapper.xml`: 向量文档SQL映射

#### 数据库初始化
- `DatabaseInitService`: 自动创建向量存储相关表

## 数据库表结构

### 知识库表 (t_knowledge_base)
```sql
CREATE TABLE t_knowledge_base (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '知识库名称',
    description TEXT COMMENT '知识库描述',
    vector_model VARCHAR(50) NOT NULL DEFAULT 'openai' COMMENT '向量模型',
    status BOOLEAN NOT NULL DEFAULT TRUE COMMENT '状态',
    tags TEXT COMMENT '标签，JSON格式存储',
    config_json TEXT COMMENT '配置信息，JSON格式存储',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
);
```

### 向量文档表 (t_vector_document)
```sql
CREATE TABLE t_vector_document (
    id VARCHAR(32) PRIMARY KEY,
    kb_id VARCHAR(32) NOT NULL COMMENT '知识库ID',
    title VARCHAR(200) NOT NULL COMMENT '文档标题',
    content LONGTEXT NOT NULL COMMENT '文档内容',
    content_type VARCHAR(50) COMMENT '内容类型',
    file_size BIGINT COMMENT '文件大小',
    file_path VARCHAR(500) COMMENT '文件路径',
    vector_model VARCHAR(50) NOT NULL COMMENT '向量模型',
    embedding LONGTEXT COMMENT '向量数据，JSON格式',
    metadata TEXT COMMENT '元数据，JSON格式',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
);
```

## API接口

### 知识库管理
- `GET /api/kb` - 查询知识库列表（支持分页、搜索）
- `POST /api/kb` - 创建知识库
- `GET /api/kb/{id}` - 获取知识库详情
- `PUT /api/kb/{id}` - 更新知识库
- `DELETE /api/kb/{id}` - 删除知识库

### 文档管理
- `GET /api/kb/{id}/documents` - 获取知识库文档列表
- `POST /api/kb/{id}/documents` - 添加文档到知识库
- `DELETE /api/kb/{id}/documents/{documentId}` - 删除知识库文档

### 命中测试
- `POST /api/kb/{id}/test` - 知识库命中测试

## 使用说明

### 1. 环境准备
- Java 17+
- MySQL 8.0+
- 确保数据库连接配置正确

### 2. 启动应用
应用启动时会自动：
1. 创建向量存储相关数据表
2. 初始化数据库结构
3. 支持数据持久化

### 3. 测试API
使用 `test-kb-database.http` 文件测试所有功能：
1. 创建知识库
2. 添加文档
3. 查询和搜索
4. 命中测试
5. 验证数据持久化

## 数据持久化验证

### 测试步骤
1. 启动应用，创建知识库和文档
2. 重启应用
3. 查询之前创建的数据
4. 验证数据是否完整保留

### 预期结果
- 所有数据完整保留
- 支持分页查询
- 支持条件搜索
- 事务操作正常

## 技术特性

### 1. 事务管理
- 使用 `@Transactional` 注解
- 支持数据一致性
- 异常时自动回滚

### 2. 逻辑删除
- 数据不会物理删除
- 支持数据恢复
- 查询时自动过滤已删除数据

### 3. 分页查询
- 支持分页参数
- 支持条件过滤
- 返回总数和分页信息

### 4. 标签处理
- 标签以JSON格式存储
- 支持标签的序列化和反序列化
- 异常处理机制

## 扩展建议

### 1. 向量化支持
- 集成向量数据库（如Milvus、Pinecone）
- 实现真正的向量相似度搜索
- 支持多种向量模型

### 2. 文件处理
- 支持更多文件格式
- 实现文件上传和存储
- 添加文件处理状态管理

### 3. 性能优化
- 添加缓存机制
- 实现批量操作
- 优化查询性能

## 故障排除

### 常见问题

1. **表创建失败**
   - 检查数据库连接
   - 确认数据库权限
   - 查看应用日志

2. **数据查询异常**
   - 检查Mapper接口
   - 确认XML映射文件
   - 验证SQL语法

3. **事务回滚**
   - 检查异常日志
   - 确认数据完整性
   - 验证业务逻辑

## 总结

通过本次重构，知识库模块实现了：
- 真正的数据持久化
- 完整的CRUD操作
- 事务管理支持
- 分页查询功能
- 逻辑删除机制

现在 `api/kb` 接口的所有操作都会记录到关系表中，数据重启后不会丢失，为后续的向量化功能奠定了坚实的基础。 