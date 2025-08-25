# 项目结构说明

## 整体架构

```
ai_flow_cursor/
├── ai-flow-parent/          # 父级Maven项目
├── ai-flow-server/          # 原有的AI Flow服务器
└── ai-flow-model/           # 新的AI模型模块（基于LangChain4j）
```

## 模块说明

### 1. ai-flow-parent
- **类型**: Maven父级项目
- **作用**: 管理子模块的依赖和版本
- **包含模块**: ai-flow-server, ai-model

### 2. ai-flow-server
- **类型**: Spring Boot应用
- **作用**: 原有的AI Flow服务器，包含工作流引擎、知识库等功能
- **依赖**: 依赖ai-model模块
- **状态**: 保持原有功能，逐步迁移到新模块

### 3. ai-flow-model
- **类型**: Spring Boot自动配置模块
- **作用**: 基于LangChain4j的AI模型管理
- **特性**: 支持多种AI模型、统一接口、配置管理
- **状态**: 新开发，可独立使用

## 详细结构

### ai-flow-model模块结构
```
ai-flow-model/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/aiflow/aimodel/
│   │   │       ├── adapter/           # 模型适配器
│   │   │       │   ├── AiModelAdapter.java
│   │   │       │   └── impl/
│   │   │       │       ├── OpenAiAdapter.java
│   │   │       │       ├── AnthropicAdapter.java
│   │   │       │       └── OllamaAdapter.java
│   │   │       ├── config/            # 配置类
│   │   │       │   ├── AiModelProperties.java
│   │   │       │   └── AiModelAutoConfiguration.java
│   │   │       ├── factory/           # 工厂类
│   │   │       │   └── AiModelFactory.java
│   │   │       ├── model/             # 模型定义
│   │   │       │   ├── AiModelType.java
│   │   │       │   └── AiModelConfig.java
│   │   │       └── service/           # 服务类
│   │   │           └── AiModelService.java
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── spring.factories   # Spring Boot自动配置
│   │       └── application-ai-model.yml
│   └── test/
│       └── java/
│           └── com/aiflow/aimodel/
│               └── AiModelModuleTest.java
├── pom.xml
└── README.md
```

### ai-flow-server模块结构
```
ai-flow-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/aiflow/server/
│   │   │       ├── ai/                # 原有AI模块（待迁移）
│   │   │       │   ├── adapter/
│   │   │       │   ├── factory/
│   │   │       └── model/
│   │   │       ├── controller/        # 控制器
│   │   │       ├── service/           # 服务层
│   │   │       ├── entity/            # 实体类
│   │   │       └── ...
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

## 依赖关系

### 依赖图
```
ai-flow-parent (父级)
├── ai-flow-server (依赖 ai-model)
└── ai-model (独立模块)
```

### 依赖说明
- **ai-flow-server** → **ai-flow-model**: 使用新的AI模型管理功能
- **ai-flow-model** → **无**: 独立模块，不依赖其他业务模块
- **ai-flow-parent**: 管理所有模块的版本和依赖

## 迁移策略

### 阶段1: 模块创建
- ✅ 创建ai-flow-model模块
- ✅ 实现核心功能
- ✅ 配置Spring Boot自动配置

### 阶段2: 依赖集成
- ✅ 更新ai-flow-server依赖
- ✅ 移除原有的LangChain4j依赖
- ✅ 添加ai-flow-model模块依赖

### 阶段3: 功能迁移
- 🔄 逐步迁移原有AI功能到新模块
- 🔄 更新配置和代码
- 🔄 保持向后兼容

### 阶段4: 测试和优化
- ⏳ 全面测试新模块功能
- ⏳ 性能优化
- ⏳ 文档完善

## 配置管理

### 环境变量
```bash
# OpenAI
export OPENAI_API_KEY=your-api-key
export OPENAI_BASE_URL=https://api.openai.com
export OPENAI_MODEL=gpt-3.5-turbo

# Anthropic
export ANTHROPIC_API_KEY=your-api-key
export ANTHROPIC_MODEL=claude-3-sonnet-20240229

# Ollama
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_MODEL=llama2
```

### 配置文件
- **application.yml**: 主配置文件
- **application-ai-model.yml**: AI模型专用配置
- **spring.factories**: Spring Boot自动配置

## 开发指南

### 添加新模型类型
1. 在`AiModelType`枚举中添加新类型
2. 实现`AiModelAdapter`接口
3. 在`AiModelProperties`中添加配置
4. 注册到Spring容器

### 扩展功能
1. 在`AiModelAdapter`接口中添加新方法
2. 在所有适配器中实现新方法
3. 在`AiModelService`中暴露新功能
4. 添加相应的测试

### 配置验证
1. 使用Bean Validation注解
2. 在`AiModelConfig`中实现验证逻辑
3. 在服务层进行配置检查

## 部署说明

### 独立部署
```bash
# 构建ai-flow-model模块
cd ai-flow-model
mvn clean install

# 在其他项目中使用
mvn dependency:get -Dartifact=com.aiflow:ai-flow-model:0.0.1-SNAPSHOT
```

### 集成部署
```bash
# 构建整个项目
mvn clean install

# 运行ai-flow-server
cd ai-flow-server
mvn spring-boot:run
```

## 监控和维护

### 日志配置
```yaml
logging:
  level:
    com.aiflow.aimodel: INFO
    dev.langchain4j: WARN
```

### 健康检查
- 模型连接状态监控
- 性能指标收集
- 错误率统计

### 故障排除
- 配置验证
- 连接测试
- 模型状态检查

## 总结

新的模块化架构提供了：
- **更好的代码组织**: 清晰的模块边界和职责分离
- **更强的扩展性**: 易于添加新的模型类型和功能
- **更高的可维护性**: 统一的接口和配置管理
- **更好的性能**: 基于专业的LangChain4j框架

建议按照迁移指南逐步进行迁移，确保系统的稳定性和功能的完整性。 