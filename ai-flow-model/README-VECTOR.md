# AI Flow 向量系统使用指南

## 概述

AI Flow 向量系统基于 LangChain4j 构建，提供了完整的向量模型和向量存储抽象化解决方案。系统支持多种向量模型（智普AI、千帆、OpenAI等）和向量存储（Milvus、Chroma、Pinecone、Weaviate等）。

## 特性

- 🚀 **抽象工厂模式**：统一的接口，支持多种向量模型和存储
- 🔧 **可配置性**：通过配置文件轻松切换不同的向量模型和存储
- 🎯 **默认支持**：默认使用 Milvus 作为向量存储
- 🌐 **多模型支持**：支持智普AI、千帆、OpenAI等主流向量模型
- 📊 **完整功能**：支持文档添加、向量搜索、集合管理等

## 架构设计

### 核心组件

1. **EmbeddingModel**：向量模型抽象接口
2. **VectorStore**：向量存储抽象接口
3. **EmbeddingModelFactory**：向量模型工厂
4. **VectorStoreFactory**：向量存储工厂
5. **VectorService**：向量服务，整合模型和存储

### 设计模式

- **抽象工厂模式**：统一管理不同类型的向量模型和存储
- **策略模式**：支持运行时切换不同的向量模型和存储
- **模板方法模式**：统一的接口定义，便于扩展

## 快速开始

### 1. 配置依赖

在 `pom.xml` 中已经包含了必要的依赖：

```xml
<!-- LangChain4j 智普AI -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-zhipu-ai</artifactId>
    <version>${langchain4j.version}</version>
</dependency>

<!-- LangChain4j 千帆 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-qianfan</artifactId>
    <version>${langchain4j.version}</version>
</dependency>

<!-- LangChain4j Milvus -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-milvus</artifactId>
    <version>${langchain4j.version}</version>
</dependency>
```

### 2. 配置文件

创建 `application-vector.yml` 配置文件：

```yaml
ai:
  vectorstore:
    default-type: milvus
    
    milvus:
      host: localhost
      port: 19530
      collection-name: ai_flow_documents
      dimension: 1536
    
  model:
    zhipu-ai:
      api-key: ${ZHIPU_AI_API_KEY:}
      embedding-model: text-embedding-v2
    
    qianfan:
      api-key: ${QIANFAN_API_KEY:}
      secret-key: ${QIANFAN_SECRET_KEY:}
      embedding-model: text-embedding-v1
    
    openai:
      api-key: ${OPENAI_API_KEY:}
      embedding-model: text-embedding-ada-002
```

### 3. 使用示例

#### 基本使用

```java
@Autowired
private VectorService vectorService;

// 创建集合
vectorService.createCollection("my_collection");

// 添加文档
Document document = Document.from("这是一个测试文档");
String documentId = vectorService.addDocument("my_collection", document, "zhipu-ai");

// 相似性搜索
List<EmbeddingMatch<TextSegment>> results = vectorService.search(
    "my_collection", "测试", 5, "zhipu-ai"
);
```

#### 使用工厂

```java
@Autowired
private EmbeddingModelFactory embeddingModelFactory;

@Autowired
private VectorStoreFactory vectorStoreFactory;

// 获取特定的向量模型
EmbeddingModel model = embeddingModelFactory.getEmbeddingModel("zhipu-ai");

// 获取特定的向量存储
VectorStore store = vectorStoreFactory.getVectorStore("milvus");

// 测试连接
Map<String, Boolean> results = vectorService.testConnections();
```

## 支持的向量模型

### 智普AI (Zhipu AI)

- **模型名称**：`text-embedding-v2`
- **向量维度**：1024
- **特点**：中文优化，性能优秀

### 千帆 (Qianfan)

- **模型名称**：`text-embedding-v1`
- **向量维度**：1024
- **特点**：阿里云服务，稳定可靠

### OpenAI

- **模型名称**：`text-embedding-ada-002`
- **向量维度**：1536
- **特点**：通用性强，支持多语言

## 支持的向量存储

### Milvus (默认)

- **类型**：开源向量数据库
- **特点**：高性能、可扩展、支持多种索引类型
- **配置**：支持主机、端口、集合名称、维度等配置

### Chroma

- **类型**：嵌入式向量数据库
- **特点**：轻量级、易于部署、支持本地存储

### Pinecone

- **类型**：云服务向量数据库
- **特点**：全托管、高可用、支持实时更新

### Weaviate

- **类型**：向量搜索引擎
- **特点**：支持语义搜索、GraphQL接口、丰富的元数据

## 高级功能

### 批量操作

```java
// 批量添加文档
List<Document> documents = Arrays.asList(
    Document.from("文档1"),
    Document.from("文档2"),
    Document.from("文档3")
);

List<String> ids = vectorService.addDocuments("my_collection", documents, "zhipu-ai");
```

### 向量搜索

```java
// 使用预计算的向量进行搜索
List<Float> queryVector = Arrays.asList(0.1f, 0.2f, 0.3f, ...);
List<EmbeddingMatch<TextSegment>> results = vectorService.searchByVector(
    "my_collection", queryVector, 10
);
```

### 集合管理

```java
// 获取集合信息
Map<String, Object> info = vectorService.getCollectionInfo("my_collection");

// 删除集合
boolean deleted = vectorService.deleteCollection("my_collection");
```

## 配置说明

### 环境变量

```bash
# 智普AI
export ZHIPU_AI_API_KEY="your_api_key"

# 千帆
export QIANFAN_API_KEY="your_api_key"
export QIANFAN_SECRET_KEY="your_secret_key"

# OpenAI
export OPENAI_API_KEY="your_api_key"
```

### 配置优先级

1. 环境变量
2. 配置文件
3. 默认值

## 测试

运行测试用例：

```bash
mvn test -Dtest=VectorServiceTest
```

测试覆盖：
- 向量模型工厂
- 向量存储工厂
- 向量服务
- 集合操作
- 文档操作
- 搜索功能

## 扩展开发

### 添加新的向量模型

1. 实现 `EmbeddingModel` 接口
2. 添加 `@Component` 注解
3. 在配置文件中添加相应配置

### 添加新的向量存储

1. 实现 `VectorStore` 接口
2. 添加 `@Component` 注解
3. 在配置文件中添加相应配置

## 故障排除

### 常见问题

1. **连接失败**：检查网络连接和配置参数
2. **API密钥错误**：验证环境变量和配置文件
3. **维度不匹配**：确保向量模型和存储的维度一致

### 日志调试

```yaml
logging:
  level:
    com.aiflow.aimodel: DEBUG
    dev.langchain4j: INFO
```

## 性能优化

1. **批量操作**：使用批量添加而不是单个添加
2. **连接池**：配置适当的连接池大小
3. **索引优化**：根据使用场景选择合适的索引类型
4. **缓存策略**：实现向量结果缓存

## 安全考虑

1. **API密钥管理**：使用环境变量或密钥管理服务
2. **网络隔离**：在生产环境中使用私有网络
3. **访问控制**：实现适当的权限控制
4. **数据加密**：敏感数据加密存储

## 总结

AI Flow 向量系统提供了完整的向量化解决方案，支持多种主流向量模型和存储，采用抽象工厂模式设计，具有良好的扩展性和可维护性。通过简单的配置即可切换不同的向量模型和存储，满足不同场景的需求。 