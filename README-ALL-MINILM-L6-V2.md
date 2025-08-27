# All-MiniLM-L6-v2 作为默认嵌入模型

## 概述

本项目已经成功集成了 `langchain4j-embeddings-all-minilm-l6-v2` 作为默认的嵌入模型。All-MiniLM-L6-v2 是一个轻量级的多语言嵌入模型，具有以下特点：

- **模型大小**: 约 80MB
- **向量维度**: 384
- **支持语言**: 多语言（包括中文、英文等）
- **性能**: 在多种语言上表现良好
- **本地运行**: 无需外部API调用

## 配置说明

### 1. Maven 依赖

在 `ai-flow-model/pom.xml` 中已添加依赖：

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
    <version>${langchain4j.version}</version>
</dependency>
```

### 2. 配置文件

在 `ai-flow-server/src/main/resources/application.yml` 中配置：

```yaml
ai:
  model:
    # All-MiniLM-L6-v2配置（默认本地嵌入模型）
    sentence-transformers:
      enable: true
      default-model: all-minilm-l6-v2
      model-path: ${SENTENCE_TRANSFORMERS_MODEL_PATH:}

# 向量数据库配置
vector:
  # 默认向量模型
  default-model: sentence-transformers
```

### 3. 环境变量

可以设置以下环境变量来自定义模型路径：

```bash
export SENTENCE_TRANSFORMERS_MODEL_PATH=/path/to/custom/model
```

## 使用方法

### 1. 自动配置

系统会自动检测并使用 All-MiniLM-L6-v2 模型。当知识库没有指定特定向量模型时，会默认使用此模型。

### 2. 手动指定

在创建知识库时，可以指定向量模型类型为 `sentence-transformers`：

```json
{
  "name": "我的知识库",
  "description": "使用All-MiniLM-L6-v2模型",
  "vectorModel": "sentence-transformers"
}
```

### 3. API 调用

向量搜索API会自动使用配置的模型：

```bash
POST /api/vector/search
{
  "kbId": "your-kb-id",
  "query": "搜索查询",
  "topK": 10,
  "scoreThreshold": 0.7
}
```

## 技术实现

### 1. 模型实现

- **类**: `AllMiniLmL6V2EmbeddingModelImpl`
- **包**: `com.aiflow.aimodel.embedding.impl`
- **接口**: `com.aiflow.aimodel.embedding.EmbeddingModel`

### 2. 适配器模式

使用 `EmbeddingModelAdapter` 将 ai-flow-model 的接口适配到 LangChain4j：

```java
public class EmbeddingModelAdapter implements dev.langchain4j.model.embedding.EmbeddingModel {
    private final com.aiflow.aimodel.embedding.EmbeddingModel aiFlowEmbeddingModel;
    
    // 实现适配逻辑
}
```

### 3. 工厂模式

通过 `EmbeddingModelFactory` 管理不同的嵌入模型：

```java
EmbeddingModel model = embeddingModelFactory.getEmbeddingModel("sentence-transformers", "all-minilm-l6-v2");
```

## 性能特点

### 1. 向量维度

- **All-MiniLM-L6-v2**: 384 维
- **OpenAI text-embedding-ada-002**: 1536 维
- **优势**: 更小的向量维度意味着更快的存储和检索

### 2. 本地处理

- **无需网络**: 所有向量化都在本地完成
- **低延迟**: 避免了API调用的网络延迟
- **成本效益**: 无需支付外部API费用

### 3. 多语言支持

- **中文**: 优秀的中文文本理解能力
- **英文**: 在英文文本上表现良好
- **其他语言**: 支持多种欧洲和亚洲语言

## 注意事项

### 1. 首次使用

首次使用时会自动下载模型文件（约80MB），请确保网络连接正常。

### 2. 内存使用

模型加载后会占用一定的内存，建议在生产环境中监控内存使用情况。

### 3. 模型更新

如需使用自定义模型，可以通过环境变量 `SENTENCE_TRANSFORMERS_MODEL_PATH` 指定模型路径。

## 故障排除

### 1. 模型加载失败

检查依赖是否正确添加，确保 `langchain4j-embeddings-all-minilm-l6-v2` 在 classpath 中。

### 2. 向量维度不匹配

确保 Milvus 集合的维度设置为 384（All-MiniLM-L6-v2）或 1536（OpenAI）。

### 3. 性能问题

如果遇到性能问题，可以考虑：
- 调整文本分块大小
- 优化向量存储配置
- 使用更强大的硬件资源

## 总结

通过集成 All-MiniLM-L6-v2 作为默认嵌入模型，我们实现了：

1. **本地化**: 无需外部API依赖
2. **多语言**: 支持中英文等多种语言
3. **高性能**: 384维向量提供良好的检索效果
4. **成本效益**: 避免外部API调用费用
5. **易于部署**: 模型文件包含在依赖中

这为AI Flow项目提供了一个强大、可靠且经济的向量化解决方案。 