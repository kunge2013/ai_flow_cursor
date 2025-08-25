# AI模块迁移指南

本文档介绍如何从原有的AI模块迁移到新的基于LangChain4j的`ai-flow-model`模块。

## 迁移概述

### 原有架构
- 基于HTTP客户端的简单适配器
- 手动管理API调用
- 有限的模型支持
- 缺乏统一的配置管理

### 新架构优势
- 基于LangChain4j的专业AI框架
- 统一的模型接口和配置
- 支持更多模型类型
- 更好的错误处理和重试机制
- 异步和流式支持

## 迁移步骤

### 1. 更新依赖

#### 原有依赖（移除）
```xml
<!-- 移除原有的AI相关依赖 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>0.27.1</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>0.27.1</version>
</dependency>
```

#### 新依赖（添加）
```xml
<!-- 添加新的ai-flow-model模块依赖 -->
<dependency>
    <groupId>com.aiflow</groupId>
    <artifactId>ai-flow-model</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 更新配置

#### 原有配置
```yaml
# 原有配置方式
openai:
  api-key: your-api-key
  base-url: https://api.openai.com
  model: gpt-3.5-turbo
```

#### 新配置
```yaml
# 新配置方式
ai:
  model:
    openai:
      api-key: your-api-key
      base-url: https://api.openai.com
      model: gpt-3.5-turbo
      max-tokens: 2048
      temperature: 0.7
      timeout: 60000
      max-retries: 3
```

### 3. 代码迁移

#### 原有代码
```java
@Service
public class MyService {
    
    @Autowired
    private AiModelAdapterFactory adapterFactory;
    
    public String generateText(String prompt) {
        AiModelAdapter adapter = adapterFactory.getAdapter("openai");
        ModelDTO modelDTO = new ModelDTO();
        modelDTO.setApiKey("your-api-key");
        modelDTO.setBaseModel("gpt-3.5-turbo");
        
        return adapter.generateText(modelDTO, prompt, 1024, 0.7);
    }
}
```

#### 新代码
```java
@Service
public class MyService {
    
    @Autowired
    private AiModelService aiModelService;
    
    public String generateText(String prompt) {
        AiModelConfig config = AiModelConfig.builder()
            .name("My OpenAI Model")
            .type(AiModelType.OPENAI)
            .baseModel("gpt-3.5-turbo")
            .apiKey("your-api-key")
            .maxTokens(1024)
            .temperature(0.7)
            .build();
            
        return aiModelService.generateText(config, prompt);
    }
    
    // 异步生成
    public CompletableFuture<String> generateTextAsync(String prompt) {
        AiModelConfig config = // ... 配置
        return aiModelService.generateTextAsync(config, prompt);
    }
    
    // 流式生成
    public Stream<String> generateTextStream(String prompt) {
        AiModelConfig config = // ... 配置
        return aiModelService.generateTextStream(config, prompt);
    }
}
```

### 4. 模型配置迁移

#### 原有ModelDTO
```java
public class ModelDTO {
    private String apiKey;
    private String baseModel;
    private String apiEndpoint;
    private Integer maxTokens;
    private Double temperature;
}
```

#### 新AiModelConfig
```java
public class AiModelConfig {
    private String id;
    private String name;
    private AiModelType type;
    private String baseModel;
    private String apiKey;
    private String secretKey;
    private String apiEndpoint;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private Integer maxRetries;
    private Long timeout;
    private Boolean enabled;
    private String description;
    private String[] tags;
    private Map<String, Object> customParams;
}
```

### 5. 适配器迁移

#### 原有适配器
```java
@Component
public class OpenAiAdapter implements AiModelAdapter {
    
    @Override
    public String generateText(ModelDTO modelDTO, String prompt, 
                             Integer maxTokens, Double temperature) {
        // 手动HTTP调用实现
        HttpClient httpClient = HttpClient.newHttpClient();
        // ... HTTP请求逻辑
    }
}
```

#### 新适配器
```java
@Component
public class OpenAiAdapter implements AiModelAdapter {
    
    @Override
    public String generateText(AiModelConfig config, String prompt) {
        ChatLanguageModel model = createModel(config);
        return model.generate(prompt);
    }
    
    private ChatLanguageModel createModel(AiModelConfig config) {
        return OpenAiChatModel.builder()
                .apiKey(config.getApiKey())
                .baseUrl(config.getApiEndpoint())
                .modelName(config.getBaseModel())
                .maxTokens(config.getMaxTokens())
                .temperature(config.getTemperature())
                .build();
    }
}
```

## 配置映射表

| 原有配置 | 新配置 | 说明 |
|---------|--------|------|
| `apiKey` | `ai.model.openai.api-key` | API密钥 |
| `baseUrl` | `ai.model.openai.base-url` | API端点 |
| `model` | `ai.model.openai.model` | 模型名称 |
| `maxTokens` | `ai.model.openai.max-tokens` | 最大Token数 |
| `temperature` | `ai.model.openai.temperature` | 温度参数 |
| - | `ai.model.openai.top-p` | Top-P参数（新增） |
| - | `ai.model.openai.timeout` | 超时时间（新增） |
| - | `ai.model.openai.max-retries` | 最大重试次数（新增） |

## 环境变量支持

新模块支持环境变量配置：

```bash
export OPENAI_API_KEY=your-api-key
export OPENAI_BASE_URL=https://api.openai.com
export OPENAI_MODEL=gpt-3.5-turbo
export OPENAI_MAX_TOKENS=2048
export OPENAI_TEMPERATURE=0.7
```

## 测试迁移

### 1. 单元测试
```java
@SpringBootTest
class AiModelServiceTest {
    
    @Autowired
    private AiModelService aiModelService;
    
    @Test
    void testGenerateText() {
        AiModelConfig config = createTestConfig();
        String result = aiModelService.generateText(config, "Hello");
        assertNotNull(result);
    }
}
```

### 2. 集成测试
```java
@SpringBootTest
@ActiveProfiles("test")
class AiModelIntegrationTest {
    
    @Test
    void testModelConnection() {
        // 测试模型连接
    }
    
    @Test
    void testTextGeneration() {
        // 测试文本生成
    }
}
```

## 常见问题

### Q: 迁移后原有的模型配置还能用吗？
A: 需要按照新的配置格式进行迁移，但配置内容基本一致。

### Q: 新模块支持哪些模型类型？
A: 支持OpenAI、Anthropic、Google Gemini、Ollama、智普AI、通义千问等。

### Q: 如何处理原有的自定义适配器？
A: 需要实现新的`AiModelAdapter`接口，并注册到Spring容器中。

### Q: 新模块的性能如何？
A: 基于LangChain4j，性能更好，支持异步和流式处理。

## 回滚计划

如果迁移过程中遇到问题，可以：

1. 保留原有代码分支
2. 逐步迁移，一个模块一个模块地切换
3. 使用特性开关控制新旧模块的使用
4. 准备回滚脚本

## 总结

迁移到新的`ai-model`模块将带来：
- 更好的代码结构和可维护性
- 更丰富的功能特性
- 更好的性能和稳定性
- 更专业的AI模型管理

建议按照本文档的步骤逐步进行迁移，确保系统的稳定性和功能的完整性。 