# 模型管理API文档

## 概述

模型管理API提供了完整的AI模型配置管理功能，包括模型的增删改查、接口测试等功能。

## 数据库表结构

### t_model 表

| 字段名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| id | BIGINT | 主键ID | AUTO_INCREMENT |
| model_name | VARCHAR(255) | 模型名称 | 必填 |
| base_model | VARCHAR(100) | 基础模型 | 必填 |
| model_type | VARCHAR(100) | 模型类型 | 必填 |
| status | VARCHAR(20) | 状态 | 'active' |
| api_endpoint | VARCHAR(500) | API地址 | 必填 |
| api_key | VARCHAR(500) | API密钥 | 必填 |
| description | TEXT | 模型描述 | 可选 |
| max_tokens | INT | 最大Token数 | 4096 |
| temperature | DECIMAL(3,2) | 温度参数 | 0.70 |
| top_p | DECIMAL(3,2) | Top P参数 | 1.00 |
| frequency_penalty | DECIMAL(3,2) | 频率惩罚 | 0.00 |
| presence_penalty | DECIMAL(3,2) | 存在惩罚 | 0.00 |
| stop_words | TEXT | 停止词(JSON格式) | 可选 |
| config_json | LONGTEXT | 其他配置参数 | 可选 |
| created_at | DATETIME | 创建时间 | CURRENT_TIMESTAMP |
| updated_at | DATETIME | 更新时间 | CURRENT_TIMESTAMP |
| deleted | TINYINT | 逻辑删除标记 | 0 |

## API接口

### 1. 分页查询模型列表

**接口地址：** `GET /api/models`

**请求参数：**
- `currentPage`: 当前页码（默认1）
- `pageSize`: 每页大小（默认10）
- `modelName`: 模型名称（模糊查询，可选）
- `modelType`: 模型类型（可选）
- `baseModel`: 基础模型（可选）
- `status`: 状态（可选）

**响应示例：**
```json
{
  "records": [
    {
      "id": 1,
      "modelName": "GPT-4文本生成模型",
      "baseModel": "gpt-4",
      "modelType": "text",
      "status": "active",
      "apiEndpoint": "https://api.openai.com/v1/chat/completions",
      "description": "OpenAI GPT-4文本生成模型",
      "maxTokens": 4096,
      "temperature": 0.7,
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "total": 1,
  "size": 10,
  "current": 1
}
```

### 2. 获取模型详情

**接口地址：** `GET /api/models/{id}`

**路径参数：**
- `id`: 模型ID

**响应示例：**
```json
{
  "id": 1,
  "modelName": "GPT-4文本生成模型",
  "baseModel": "gpt-4",
  "modelType": "text",
  "status": "active",
  "apiEndpoint": "https://api.openai.com/v1/chat/completions",
  "apiKey": "sk-...",
  "description": "OpenAI GPT-4文本生成模型",
  "maxTokens": 4096,
  "temperature": 0.7,
  "topP": 1.0,
  "frequencyPenalty": 0.0,
  "presencePenalty": 0.0,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### 3. 创建模型

**接口地址：** `POST /api/models`

**请求体：**
```json
{
  "modelName": "新模型",
  "baseModel": "gpt-3.5",
  "modelType": "text",
  "status": "active",
  "apiEndpoint": "https://api.openai.com/v1/chat/completions",
  "apiKey": "sk-...",
  "description": "模型描述",
  "maxTokens": 2048,
  "temperature": 0.8,
  "topP": 0.9,
  "frequencyPenalty": 0.0,
  "presencePenalty": 0.0
}
```

**响应：** `true` 表示成功

### 4. 更新模型

**接口地址：** `PUT /api/models/{id}`

**路径参数：**
- `id`: 模型ID

**请求体：** 同创建模型

**响应：** `true` 表示成功

### 5. 删除模型

**接口地址：** `DELETE /api/models/{id}`

**路径参数：**
- `id`: 模型ID

**响应：** `true` 表示成功

### 6. 测试模型接口

**接口地址：** `POST /api/models/test`

**请求体：**
```json
{
  "modelId": 1,
  "apiEndpoint": "https://api.openai.com/v1/chat/completions",
  "apiKey": "sk-...",
  "testInput": "Hello, this is a test message.",
  "maxTokens": 100,
  "temperature": 0.7
}
```

**响应：** 测试结果字符串

### 7. 获取模型类型列表

**接口地址：** `GET /api/models/types`

**响应：**
```json
["text", "image", "speech", "multimodal", "code", "translation"]
```

### 8. 获取基础模型列表

**接口地址：** `GET /api/models/base-models`

**响应：**
```json
["gpt-4", "gpt-3.5", "claude", "gemini", "wenxin", "qwen"]
```

## 部署说明

### 1. 数据库初始化

执行以下SQL脚本创建表结构和初始数据：

```bash
# 创建数据库和表结构
mysql -u root -p < src/main/resources/schema.sql

# 初始化模型数据
mysql -u root -p < src/main/resources/init-model-data.sql
```

### 2. 启动服务

```bash
# 编译项目
mvn clean compile

# 启动服务
mvn spring-boot:run
```

### 3. 验证服务

服务启动后，访问以下地址验证：

- Swagger API文档：http://localhost:8081/swagger-ui.html
- 健康检查：http://localhost:8081/actuator/health

## 测试说明

### 1. 使用HTTP文件测试

项目根目录下的 `test-model-api.http` 文件包含了所有API的测试用例，可以在支持HTTP文件的IDE中直接运行。

### 2. 使用Postman测试

导入HTTP文件到Postman中，或者根据API文档手动创建请求。

### 3. 前端联调测试

启动前端项目，在模型管理页面中进行功能测试：

- 模型列表查询
- 新增模型
- 编辑模型
- 删除模型
- 模型接口测试

## 注意事项

1. **API密钥安全**：生产环境中API密钥应该加密存储，不应明文显示
2. **接口限流**：建议对模型测试接口添加限流保护
3. **错误处理**：所有接口都包含完整的错误处理和日志记录
4. **数据验证**：使用JSR-303注解进行请求参数验证
5. **分页查询**：支持多条件组合查询和分页

## 扩展功能

1. **模型版本管理**：支持模型配置的版本控制
2. **使用统计**：记录模型的使用次数和性能指标
3. **权限控制**：基于角色的模型访问控制
4. **批量操作**：支持批量导入、导出模型配置
5. **模型监控**：实时监控模型API的可用性和性能 