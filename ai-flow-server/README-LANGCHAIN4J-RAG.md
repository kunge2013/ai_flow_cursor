# LangChain4j RAG 集成说明

本项目已成功集成 LangChain4j 的 RAG（检索增强生成）功能，提供了更强大的文档处理和向量搜索能力。

## 功能特性

### 1. 智能文档解析
- 支持多种文档格式：PDF、Word、Excel、TXT、Markdown等
- 使用 Apache Tika 进行文档内容提取
- 自动识别文档类型和编码

### 2. 智能文本分割
- 使用 LangChain4j 的递归分割器
- 可配置的chunk大小和重叠度
- 保持语义完整性的分割策略

### 3. 向量化处理
- 支持多种嵌入模型：OpenAI、智普AI等
- 异步向量化处理，提高性能
- 自动向量维度管理

### 4. 向量存储
- 集成 Milvus 向量数据库
- 支持知识库级别的向量隔离
- 高效的相似度搜索

### 5. RAG 检索
- 基于向量相似度的智能检索
- 支持阈值过滤和topK查询
- 实时检索结果

## 配置说明

### 环境变量配置

```bash
# OpenAI配置
export OPENAI_API_KEY=your_openai_api_key

# 智普AI配置
export ZHIPU_API_KEY=your_zhipu_api_key

# Milvus配置
export MILVUS_HOST=localhost
export MILVUS_PORT=19530
export MILVUS_USERNAME=your_username
export MILVUS_PASSWORD=your_password
```

### 配置文件

在 `application-rag.yml` 中配置相关参数：

```yaml
ai:
  model:
    openai:
      api-key: ${OPENAI_API_KEY:}
      embedding-model: text-embedding-ada-002
    zhipu-ai:
      api-key: ${ZHIPU_API_KEY:}
      embedding-model: text-embedding-v2

milvus:
  host: ${MILVUS_HOST:localhost}
  port: ${MILVUS_PORT:19530}
  collection: ai_flow_documents

langchain4j:
  document:
    chunk-size: 1000
    chunk-overlap: 200
    max-chunks: 100
```

## API 接口

### 1. 文档上传接口

#### 文件上传
```http
POST /api/kb/{id}/documents/upload
Content-Type: multipart/form-data

file: [文件]
```

#### 文档内容上传
```http
POST /api/kb/{id}/documents
Content-Type: application/json

{
  "title": "文档标题",
  "content": "文档内容",
  "type": "text",
  "size": 1024
}
```

### 2. RAG 搜索接口

```http
POST /api/kb/{id}/rag/search
Content-Type: application/json

{
  "query": "搜索查询",
  "topK": 5,
  "scoreThreshold": 0.7
}
```

### 3. RAG 统计接口

```http
GET /api/kb/{id}/rag/statistics
```

## 使用流程

### 1. 启动服务

```bash
# 启动 Milvus
docker-compose -f docker-compose-milvus.yml up -d

# 启动后端服务
mvn spring-boot:run -Dspring.profiles.active=rag

# 启动前端
cd ../ai-flow-ui
npm run dev
```

### 2. 创建知识库

1. 访问 `http://localhost:5173/kb`
2. 点击"创建知识库"
3. 填写知识库信息，选择向量模型
4. 保存知识库

### 3. 上传文档

1. 点击知识库卡片进入详情页
2. 选择"文档"标签页
3. 点击"文件上传"或"文档库上传"
4. 选择文件并上传
5. 系统自动进行RAG处理

### 4. 测试检索

1. 选择"命中测试"标签页
2. 输入测试查询
3. 调整搜索参数（topK、阈值）
4. 查看检索结果

## 技术架构

```
前端 (Vue3) 
    ↓
后端 (Spring Boot)
    ↓
LangChain4j RAG Service
    ↓
├── 文档解析 (Apache Tika)
├── 文本分割 (DocumentSplitters)
├── 向量化 (EmbeddingModel)
├── 向量存储 (Milvus)
└── 检索增强 (RAG)
```

## 性能优化

### 1. 异步处理
- 文档上传后立即返回，RAG处理异步进行
- 使用线程池管理并发处理

### 2. 缓存策略
- 嵌入模型结果缓存
- 向量存储查询缓存

### 3. 批量处理
- 支持批量文档上传
- 批量向量化处理

## 监控和日志

### 1. 日志级别
```yaml
logging:
  level:
    dev.langchain4j: DEBUG
    com.aiflow.server.service.LangChainRagService: DEBUG
```

### 2. 性能监控
- 文档处理时间
- 向量化耗时
- 检索响应时间

### 3. 错误处理
- 详细的错误日志
- 失败重试机制
- 状态跟踪

## 常见问题

### 1. Milvus 连接失败
- 检查 Milvus 服务是否启动
- 验证连接参数配置
- 检查网络连通性

### 2. 向量化失败
- 检查 API Key 配置
- 验证模型名称
- 查看错误日志

### 3. 检索结果为空
- 检查文档是否成功向量化
- 调整相似度阈值
- 验证查询内容

## 扩展功能

### 1. 支持更多模型
- 本地模型集成
- 其他云服务提供商
- 自定义模型

### 2. 高级检索
- 混合检索策略
- 语义理解增强
- 上下文感知

### 3. 知识图谱
- 实体关系提取
- 图数据库集成
- 推理能力增强

## 总结

通过集成 LangChain4j RAG 功能，本项目实现了：

1. **智能文档处理**：自动解析、分割、向量化
2. **高效向量存储**：基于 Milvus 的高性能存储
3. **精准信息检索**：基于语义相似度的智能搜索
4. **可扩展架构**：支持多种模型和存储后端
5. **生产就绪**：完善的错误处理和监控机制

这将显著提升知识库系统的智能化水平和用户体验。
