# AI Flow 向量模块

## 概述

AI Flow 向量模块是基于 LangChain4j 构建的向量化服务模块，提供文档向量化、向量搜索和 RAG（检索增强生成）功能。

## 功能特性

### 1. 向量服务 (VectorService)

- **文档向量化**: 将文本内容转换为向量表示
- **批量向量化**: 支持批量处理多个文档
- **向量搜索**: 基于相似度的向量搜索
- **向量管理**: 保存、更新、删除文档向量
- **统计信息**: 获取向量存储的统计信息

### 2. RAG 服务 (RagService)

- **文档处理**: 自动解析和分割文档内容
- **智能分块**: 基于语义的文本分块策略
- **异步处理**: 支持异步 RAG 处理
- **相似度搜索**: 基于 RAG 的智能搜索

## 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.aiflow</groupId>
    <artifactId>ai-flow-model</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 启用模块

在 Spring Boot 主类上添加注解：

```java
@EnableAimodel
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. 配置向量模型

在 `application.yml` 中配置：

```yaml
ai:
  model:
    openai:
      api-key: your-openai-api-key
      embedding-model: text-embedding-ada-002
    zhipu-ai:
      api-key: your-zhipu-api-key
      embedding-model: text-embedding-v2

milvus:
  host: localhost
  port: 19530
  collection: ai_flow_documents
```

### 4. 使用向量服务

```java
@Service
public class DocumentService {
    
    @Autowired
    private VectorService vectorService;
    
    @Autowired
    private RagService ragService;
    
    public void processDocument(String kbId, String content) {
        // 向量化文档
        VectorService.VectorEmbeddingResult embedding = 
            vectorService.embedDocument(content, "openai");
        
        // 保存向量
        vectorService.saveDocumentVector(
            embedding.getDocumentId(), 
            kbId, 
            content, 
            "openai"
        );
    }
    
    public List<VectorService.VectorSearchResult> searchSimilar(
            String query, String kbId) {
        return vectorService.searchSimilar(query, kbId, 10, 0.7);
    }
}
```

### 5. 使用 RAG 服务

```java
@Service
public class KnowledgeBaseService {
    
    @Autowired
    private RagService ragService;
    
    public void processDocument(String kbId, String fileName, String content) {
        // 处理文档并进行 RAG
        RagService.DocumentProcessResult result = 
            ragService.processDocument(kbId, fileName, content, "text");
        
        if ("success".equals(result.getStatus())) {
            log.info("文档处理成功，生成了 {} 个chunks", result.getTotalChunks());
        }
    }
    
    public VectorService.VectorSearchResult searchKnowledge(
            String query, String kbId) {
        return ragService.searchSimilar(query, kbId, 5, 0.8);
    }
}
```

## 架构设计

### 核心接口

1. **VectorService**: 向量化服务接口
2. **RagService**: RAG 服务接口
3. **EmbeddingModel**: 嵌入模型接口
4. **VectorStore**: 向量存储接口

### 工厂模式

- **EmbeddingModelFactory**: 嵌入模型工厂
- **VectorStoreFactory**: 向量存储工厂

### 自动配置

模块提供 Spring Boot 自动配置，自动注册相关 Bean：

- `VectorService`
- `RagService`
- `EmbeddingModelFactory`
- `VectorStoreFactory`

## 支持的向量模型

### OpenAI
- 模型: `text-embedding-ada-002`
- 维度: 1536
- 特点: 高质量英文向量化

### 智普 AI
- 模型: `text-embedding-v2`
- 维度: 1024
- 特点: 优秀的中文向量化能力

### Ollama
- 模型: `nomic-embed-text`
- 维度: 768
- 特点: 本地部署，隐私保护

## 支持的向量存储

### Milvus
- 类型: 分布式向量数据库
- 特点: 高性能、可扩展
- 适用场景: 大规模生产环境

### 内存存储
- 类型: 基于内存的向量存储
- 特点: 快速、简单
- 适用场景: 开发测试、小规模应用

## 配置选项

### 向量模型配置

```yaml
ai:
  model:
    # OpenAI 配置
    openai:
      api-key: ${OPENAI_API_KEY}
      embedding-model: text-embedding-ada-002
      timeout: 30s
      
    # 智普 AI 配置
    zhipu-ai:
      api-key: ${ZHIPU_API_KEY}
      embedding-model: text-embedding-v2
      timeout: 30s
      
    # Ollama 配置
    ollama:
      base-url: http://localhost:11434
      model: nomic-embed-text
```

### 向量存储配置

```yaml
milvus:
  host: localhost
  port: 19530
  collection: ai_flow_documents
  username: root
  password: Milvus
  database: default
```

## 最佳实践

### 1. 文档分块策略

- **固定长度**: 适合结构化文档
- **语义分块**: 基于句子边界，保持语义完整性
- **重叠分块**: 避免重要信息丢失

### 2. 向量搜索优化

- **阈值设置**: 根据应用场景调整相似度阈值
- **结果数量**: 平衡准确性和性能
- **缓存策略**: 对频繁查询进行缓存

### 3. 性能优化

- **异步处理**: 使用异步方式处理大量文档
- **批量操作**: 批量处理提高效率
- **连接池**: 合理配置数据库连接池

## 故障排除

### 常见问题

1. **API 调用失败**
   - 检查 API Key 配置
   - 验证网络连接
   - 查看 API 配额

2. **向量存储连接失败**
   - 检查数据库服务状态
   - 验证连接参数
   - 查看防火墙设置

3. **性能问题**
   - 调整线程池大小
   - 优化分块策略
   - 使用缓存机制

### 日志配置

```yaml
logging:
  level:
    com.aiflow.aimodel: DEBUG
    com.aiflow.aimodel.service: INFO
    com.aiflow.aimodel.vectorstore: DEBUG
```

## 扩展开发

### 添加新的向量模型

1. 实现 `EmbeddingModel` 接口
2. 在工厂类中注册新模型
3. 添加相应的配置支持

### 添加新的向量存储

1. 实现 `VectorStore` 接口
2. 在工厂类中注册新存储
3. 添加相应的配置支持

## 版本历史

- **0.0.1-SNAPSHOT**: 初始版本，支持基本的向量化和 RAG 功能

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进这个模块。

## 许可证

本项目采用 MIT 许可证。
