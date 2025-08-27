# 智普AI适配器快速开始指南

## 🚀 快速开始

### 1. 获取API密钥

1. 访问 [智普AI开放平台](https://open.bigmodel.cn/)
2. 注册账号并完成实名认证
3. 创建应用获取API密钥
4. 选择合适的模型（推荐：glm-4）

### 2. 配置环境变量

```bash
# Windows
set ZHIPU_AI_API_KEY=your-api-key-here

# Linux/Mac
export ZHIPU_AI_API_KEY=your-api-key-here
```

### 3. 配置文件

在 `application.yml` 中添加：

```yaml
ai:
  model:
    zhipu-ai:
      api-key: "${ZHIPU_AI_API_KEY}"
      base-model: "glm-4"
      max-tokens: 2048
      temperature: 0.7
      top-p: 0.9
```

### 4. 代码示例

```java
@RestController
@RequestMapping("/api/ai")
public class AiController {
    
    @Autowired
    private AiModelService aiModelService;
    
    @PostMapping("/generate")
    public String generateText(@RequestBody GenerateRequest request) {
        AiModelConfig config = new AiModelConfig();
        config.setType(AiModelType.ZHIPU_AI);
        config.setApiKey(System.getenv("ZHIPU_AI_API_KEY"));
        config.setBaseModel("glm-4");
        config.setMaxTokens(2048);
        
        return aiModelService.generateText(config, request.getPrompt());
    }
}
```

## 📋 功能特性

- ✅ 文本生成（同步/异步）
- ✅ 流式输出
- ✅ 参数调优
- ✅ 连接测试
- ✅ 错误处理
- ✅ 日志记录

## 🔧 配置参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `api-key` | String | - | API密钥（必需） |
| `base-model` | String | - | 模型名称（必需） |
| `max-tokens` | Integer | 2048 | 最大生成token数 |
| `temperature` | Double | 0.7 | 温度参数（0.0-1.0） |
| `top-p` | Double | 0.9 | 核采样参数（0.0-1.0） |
| `timeout` | Integer | 30000 | 请求超时时间（毫秒） |
| `max-retries` | Integer | 3 | 最大重试次数 |

## 🧪 测试

运行测试：

```bash
# 运行所有测试
mvn test

# 运行智普AI适配器测试
mvn test -Dtest=ZhipuAiAdapterTest
```

## 📚 更多信息

- [完整文档](README-ZHIPU-AI.md)
- [配置示例](src/main/resources/application-zhipu-ai.yml)
- [测试用例](src/test/java/com/aiflow/aimodel/ZhipuAiAdapterTest.java)

## 🆘 常见问题

### Q: API调用失败怎么办？
A: 检查API密钥是否正确，网络连接是否正常，模型名称是否正确。

### Q: 如何选择合适的模型？
A: 
- `glm-4`: 最新最强，适合复杂任务
- `glm-3-turbo`: 快速响应，适合对话
- `glm-4v`: 多模态，支持图像理解

### Q: 如何优化生成质量？
A: 调整 `temperature` 和 `top-p` 参数：
- 降低 `temperature` 提高一致性
- 提高 `top-p` 增加创造性

## 📞 技术支持

- 智普AI官方文档：https://open.bigmodel.cn/doc/api
- 项目Issues：https://github.com/your-repo/issues
- 开发者社区：https://community.zhipuai.cn/
