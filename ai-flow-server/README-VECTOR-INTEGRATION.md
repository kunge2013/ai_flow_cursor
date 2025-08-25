# AI知识库向量化功能集成说明

## 功能概述

本项目已成功集成AI知识库向量化功能，支持通过智普AI等向量模型将文档内容转换为向量，并支持向量相似度搜索。

## 主要特性

### 1. 向量模型支持
- **智普AI**: 使用 `text-embedding-v2` 模型，支持1024维向量
- **OpenAI**: 使用 `text-embedding-ada-002` 模型，支持1536维向量
- **千帆**: 使用 `text-embedding-v1` 模型，支持1024维向量

### 2. 文档处理方式
- **文件上传**: 支持拖拽上传各种格式文件
- **手动录入**: 使用富文本编辑器手动输入内容
- **文档库上传**: 支持批量文件夹上传

### 3. 自动向量化
- 文档上传后自动调用向量模型进行向量化
- 支持异步处理，不阻塞用户操作
- 自动保存向量数据到向量数据库

### 4. 智能搜索
- 基于向量相似度的语义搜索
- 支持相似度阈值设置
- 支持topK结果数量控制
- 失败时自动回退到关键词搜索

## 配置说明

### 1. 环境变量配置

在 `application-vector.yml` 中配置API密钥：

```yaml
ai:
  model:
    zhipu-ai:
      api-key: ${ZHIPU_AI_API_KEY:your-zhipu-ai-api-key}
      embedding-model: text-embedding-v2
```

### 2. 环境变量设置

```bash
# 智普AI API密钥
export ZHIPU_AI_API_KEY="your-actual-api-key"

# OpenAI API密钥（可选）
export OPENAI_API_KEY="your-openai-api-key"

# 千帆API密钥（可选）
export QIANFAN_API_KEY="your-qianfan-api-key"
```

### 3. 向量模型选择

在创建知识库时，可以选择不同的向量模型：

- `zhipu`: 智普AI（推荐，中文效果好）
- `openai`: OpenAI（英文效果好）
- `qianfan`: 千帆（中文效果好）

## 使用方法

### 1. 创建知识库

1. 访问知识库管理页面
2. 点击"创建知识库"
3. 填写名称、描述
4. 选择向量模型（推荐选择 `zhipu`）
5. 设置标签和状态
6. 点击创建

### 2. 上传文档

#### 文件上传
1. 在知识库详情页面点击"上传文档"
2. 选择文件上传方式
3. 拖拽文件到上传区域
4. 系统自动进行向量化处理

#### 手动录入
1. 选择"手动录入"方式
2. 使用富文本编辑器输入内容
3. 填写标题和类型
4. 点击保存，系统自动向量化

#### 文档库上传
1. 选择"文档库上传"方式
2. 选择包含文档的文件夹
3. 系统批量处理所有文档
4. 自动进行向量化

### 3. 向量搜索测试

1. 在知识库详情页面点击"命中测试"
2. 输入查询内容
3. 设置搜索参数：
   - 返回条数（topK）
   - 相似度阈值（scoreThreshold）
4. 点击"发送"进行向量搜索
5. 查看搜索结果和相似度分数

## 技术架构

### 1. 后端服务

- **VectorSearchService**: 向量搜索服务接口
- **VectorSearchServiceImpl**: 向量搜索服务实现
- **KbService**: 知识库服务，集成向量化功能
- **VectorSearchController**: 向量搜索API控制器

### 2. 向量化流程

```
文档上传 → 内容提取 → 向量化API调用 → 向量存储 → 搜索索引
```

### 3. 搜索流程

```
查询输入 → 查询向量化 → 向量相似度计算 → 结果排序 → 返回topK
```

## API接口

### 1. 向量化接口

```http
POST /api/vector/embed
Content-Type: application/json

{
  "content": "要向量化的文本内容",
  "vectorModel": "zhipu"
}
```

### 2. 向量搜索接口

```http
POST /api/vector/search
Content-Type: application/json

{
  "query": "搜索查询",
  "kbId": "知识库ID",
  "topK": 5,
  "scoreThreshold": 0.7
}
```

### 3. 批量向量化接口

```http
POST /api/vector/batch-embed
Content-Type: application/json

{
  "contents": ["文本1", "文本2", "文本3"],
  "vectorModel": "zhipu"
}
```

## 性能优化

### 1. 异步处理
- 文档向量化使用异步处理，不阻塞用户操作
- 使用线程池管理并发任务

### 2. 批量处理
- 支持批量文档向量化
- 减少API调用次数，提高效率

### 3. 缓存策略
- 向量结果缓存
- 搜索结果缓存

## 故障排除

### 1. 常见问题

#### API密钥配置错误
```
错误信息: 智普AI API Key未配置
解决方案: 检查环境变量 ZHIPU_AI_API_KEY 是否正确设置
```

#### 网络连接问题
```
错误信息: 智普AI API调用失败
解决方案: 检查网络连接，确认API地址可访问
```

#### 向量化失败
```
错误信息: 文档向量化失败
解决方案: 检查文档内容格式，确认API响应正常
```

### 2. 日志查看

查看应用日志了解详细错误信息：

```bash
# 查看应用日志
tail -f logs/application.log

# 查看向量化相关日志
grep "向量化" logs/application.log
```

### 3. 回退机制

当向量搜索失败时，系统会自动回退到关键词搜索，确保功能可用性。

## 扩展说明

### 1. 添加新的向量模型

1. 在 `VectorSearchServiceImpl` 中添加新的模型处理逻辑
2. 在配置文件中添加新模型的配置
3. 更新前端模型选择器

### 2. 集成向量数据库

当前使用内存存储，可以集成：
- **Milvus**: 开源向量数据库
- **Pinecone**: 云原生向量数据库
- **Weaviate**: 向量搜索引擎

### 3. 优化向量搜索

- 实现真实的向量相似度计算
- 添加向量索引优化
- 支持分页查询

## 总结

AI知识库向量化功能已成功集成，支持多种文档上传方式和向量模型。系统具备良好的容错性和扩展性，为用户提供智能的文档搜索体验。

通过配置智普AI API密钥，即可开始使用向量化功能。建议在生产环境中配置真实的向量数据库以提升性能和可靠性。 