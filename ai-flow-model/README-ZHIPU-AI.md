# 智普AI适配器使用说明

## 概述

智普AI适配器（ZhipuAiAdapter）是基于HTTP客户端实现的智普AI模型接口适配器，支持调用智普AI的GLM系列模型进行文本生成。

## 特性

- 支持智普AI GLM系列模型
- 完整的文本生成功能（同步/异步）
- 支持自定义参数（maxTokens、temperature、topP）
- 连接测试和状态检查
- 错误处理和日志记录

## 配置要求

### 必需配置

- `type`: 必须设置为 `ZHIPU_AI`
- `apiKey`: 智普AI的API密钥
- `baseModel`: 模型名称（如：glm-4、glm-3-turbo等）

### 可选配置

- `apiEndpoint`: API端点（默认：https://open.bigmodel.cn/api/paas/v4/chat/completions）
- `maxTokens`: 最大生成token数
- `temperature`: 温度参数（0.0-1.0）
- `topP`: 核采样参数（0.0-1.0）
- `timeout`: 请求超时时间
- `maxRetries`: 最大重试次数

## 使用示例

### 1. 基本配置

```yaml
# application.yml
ai:
  model:
    zhipu-ai:
      api-key: "your-api-key-here"
      base-model: "glm-4"
      max-tokens: 2048
      temperature: 0.7
      top-p: 0.9
```

### 2. Java代码使用

```java
@Autowired
private AiModelService aiModelService;

// 创建配置
AiModelConfig config = new AiModelConfig();
config.setType(AiModelType.ZHIPU_AI);
config.setApiKey("your-api-key");
config.setBaseModel("glm-4");
config.setMaxTokens(2048);
config.setTemperature(0.7);
config.setTopP(0.9);

// 测试连接
String testResult = aiModelService.testConnection(config);

// 生成文本
String response = aiModelService.generateText(config, "你好，请介绍一下自己");
```

### 3. 异步调用

```java
CompletableFuture<String> future = aiModelService.generateTextAsync(config, "请写一首诗");
String result = future.get(); // 等待结果
```

## API接口

### 文本生成

- `generateText(config, prompt)`: 同步生成文本
- `generateTextAsync(config, prompt)`: 异步生成文本
- `generateText(config, prompt, maxTokens, temperature, topP)`: 带参数生成文本

### 连接测试

- `testConnection(config)`: 测试API连接
- `isAvailable(config)`: 检查模型是否可用
- `getModelStatus(config)`: 获取模型状态

### 模型信息

- `getModelInfo(config)`: 获取模型详细信息

## 错误处理

适配器会处理以下常见错误：

- API密钥无效
- 网络连接失败
- API响应解析错误
- 模型不可用
- 参数验证失败

所有错误都会记录详细的日志信息，并抛出RuntimeException异常。

## 注意事项

1. **API密钥安全**: 请妥善保管API密钥，不要在代码中硬编码
2. **请求频率**: 注意智普AI的API调用频率限制
3. **模型选择**: 根据需求选择合适的模型版本
4. **参数调优**: temperature和topP参数会影响生成文本的创造性和一致性
5. **错误重试**: 建议实现适当的重试机制处理临时性错误

## 支持的模型

- `glm-4`: 最新的大语言模型
- `glm-3-turbo`: 快速响应模型
- `glm-4v`: 多模态模型
- 其他智普AI官方支持的模型

## 更新日志

- v1.0.0: 初始版本，支持基本的文本生成功能
- 支持同步和异步调用
- 完整的错误处理和日志记录
- 支持自定义参数配置

## 技术支持

如有问题，请参考：
- 智普AI官方文档：https://open.bigmodel.cn/doc/api
- 项目Issues页面
- 开发者社区
