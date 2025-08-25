# AI Flow Model Module

基于LangChain4j的AI模型管理模块，提供统一的AI模型接口和多种模型适配器。

## 特性

- 🚀 基于LangChain4j，支持多种AI模型
- 🔧 统一的模型配置和管理接口
- 📡 支持同步/异步/流式文本生成
- 🏭 工厂模式，易于扩展新的模型类型
- ⚙️ Spring Boot自动配置
- 🧪 完整的模型连接测试和状态检查

## 支持的模型类型

- **OpenAI**: GPT系列模型
- **Anthropic**: Claude系列模型  
- **Google Gemini**: Gemini系列模型
- **Ollama**: 本地开源模型
- **智普AI**: GLM系列模型
- **通义千问**: 阿里千问系列模型
- **Hugging Face**: 开源模型
- **自定义模型**: 支持自定义配置

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.aiflow</groupId>
    <artifactId>ai-flow-model</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 配置模型

在`application.yml`中配置：

```yaml
ai:
  model:
    openai:
      api-key: your-openai-api-key
      base-url: https://api.openai.com
      model: gpt-3.5-turbo
      max-tokens: 2048
      temperature: 0.7
      timeout: 60000
      max-retries: 3
    
    anthropic:
      api-key: your-anthropic-api-key
      model: claude-3-sonnet-20240229
      max-tokens: 4096
    
    ollama:
      base-url: http://localhost:11434
      model: llama2
```

### 3. 使用服务

```java
@Service
public class MyService {
    
    @Autowired
    private AiModelService aiModelService;
    
    public String generateText() {
        AiModelConfig config = AiModelConfig.builder()
            .name("My OpenAI Model")
            .type(AiModelType.OPENAI)
            .baseModel("gpt-3.5-turbo")
            .apiKey("your-api-key")
            .build();
            
        return aiModelService.generateText(config, "Hello, world!");
    }
    
    public CompletableFuture<String> generateTextAsync() {
        AiModelConfig config = // ... 配置
        return aiModelService.generateTextAsync(config, "Hello, world!");
    }
}
```

## 核心组件

### AiModelAdapter

模型适配器接口，每种模型类型都需要实现此接口：

```java
public interface AiModelAdapter {
    AiModelType getSupportedType();
    boolean isValidConfig(AiModelConfig config);
    String generateText(AiModelConfig config, String prompt);
    CompletableFuture<String> generateTextAsync(AiModelConfig config, String prompt);
    Stream<String> generateTextStream(AiModelConfig config, String prompt);
    // ... 其他方法
}
```

### AiModelFactory

模型工厂，管理不同类型的模型适配器：

```java
@Component
public class AiModelFactory {
    public AiModelAdapter getAdapter(AiModelType modelType);
    public AiModelAdapter getAdapter(AiModelConfig config);
    public List<AiModelType> getAvailableModelTypes();
}
```

### AiModelService

统一的模型服务接口：

```java
@Service
public class AiModelService {
    public String generateText(AiModelConfig config, String prompt);
    public CompletableFuture<String> generateTextAsync(AiModelConfig config, String prompt);
    public Stream<String> generateTextStream(AiModelConfig config, String prompt);
    public boolean isValidConfig(AiModelConfig config);
    public boolean isModelAvailable(AiModelConfig config);
}
```

## 扩展新模型

### 1. 创建适配器

```java
@Component
public class CustomModelAdapter implements AiModelAdapter {
    
    @Override
    public AiModelType getSupportedType() {
        return AiModelType.CUSTOM;
    }
    
    @Override
    public String generateText(AiModelConfig config, String prompt) {
        // 实现文本生成逻辑
        return "Generated text";
    }
    
    // 实现其他必要方法...
}
```

### 2. 添加模型类型

在`AiModelType`枚举中添加新类型：

```java
public enum AiModelType {
    // ... 现有类型
    CUSTOM_MODEL("custom-model", "自定义模型", "自定义模型描述");
}
```

### 3. 添加配置属性

在`AiModelProperties`中添加配置：

```java
@Data
public class AiModelProperties {
    private CustomModel customModel = new CustomModel();
    
    @Data
    public static class CustomModel {
        private String apiKey;
        private String baseUrl;
        // ... 其他配置
    }
}
```

## 配置说明

### 通用配置

- `max-tokens`: 最大生成token数
- `temperature`: 温度参数，控制随机性
- `top-p`: Top-P参数，控制词汇选择
- `timeout`: 超时时间（毫秒）
- `max-retries`: 最大重试次数

### 模型特定配置

- **OpenAI**: `api-key`, `base-url`, `model`
- **Anthropic**: `api-key`, `base-url`, `model`
- **Google Gemini**: `api-key`, `base-url`, `model`
- **Ollama**: `base-url`, `model`
- **智普AI**: `api-key`, `base-url`, `model`
- **通义千问**: `api-key`, `secret-key`, `base-url`, `model`

## 最佳实践

1. **配置验证**: 使用前验证模型配置的有效性
2. **错误处理**: 实现适当的错误处理和重试机制
3. **资源管理**: 合理设置超时和重试参数
4. **监控**: 监控模型性能和可用性
5. **安全**: 妥善保管API密钥，使用环境变量

## 故障排除

### 常见问题

1. **连接失败**: 检查API密钥和端点URL
2. **超时错误**: 调整timeout参数
3. **模型不可用**: 检查模型名称和API配额
4. **配置无效**: 验证配置参数

### 调试模式

启用调试日志：

```yaml
logging:
  level:
    com.aiflow.aimodel: DEBUG
```

## 许可证

本项目采用MIT许可证。 