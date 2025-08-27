# 🚨 "未找到可用的向量存储" 错误解决方案

## 🔍 错误分析

### 错误信息
```
向量搜索失败: 向量相似度搜索失败: 未找到可用的向量存储
```

### 问题根源
1. **配置缺失**: 代码中使用 `@Value("${milvus.host:localhost}")` 但配置文件中缺少 `milvus.host` 配置
2. **配置文件不完整**: `application-vector.yml` 中缺少必要的 Milvus 配置
3. **服务未启动**: Milvus 服务可能未运行或连接失败

## 🛠️ 已实施的解决方案

### 1. 配置文件修复

**修复前的问题**:
- 代码使用 `@Value("${milvus.host:localhost}")`
- 但配置文件中没有 `milvus.host` 配置

**修复后的配置** (`application-vector.yml`):
```yaml
# Milvus 配置（兼容代码中的 @Value 注解）
milvus:
  host: localhost
  port: 19530
  collection: ai_flow_documents

# 向量存储配置
vector:
  storage:
    type: milvus  # 从 memory 改为 milvus
    milvus:
      host: localhost
      port: 19530
      collection: ai_flow_vectors
```

### 2. 集合命名策略优化

**新策略**: 在集合名称中包含模型类型，避免维度冲突
- **All-MiniLM-L6-v2**: `ai_flow_documents_{kbId}_minilm` (384维)
- **OpenAI**: `ai_flow_documents_{kbId}_openai` (1536维)

### 3. 诊断和测试工具

创建了以下工具来帮助诊断和测试：
- `diagnose-milvus.sh` - Milvus 连接诊断脚本
- `test-vector-search.sh` - 向量搜索功能测试脚本
- `cleanup-milvus-collections.sh` - 旧集合清理脚本

## 📋 解决步骤

### 步骤1: 检查 Milvus 服务状态
```bash
./diagnose-milvus.sh
```

### 步骤2: 启动 Milvus 服务（如果未运行）
```bash
cd ai-flow-server
./start-milvus.sh
```

### 步骤3: 清理旧集合（如果存在维度冲突）
```bash
./cleanup-milvus-collections.sh
```

### 步骤4: 启动应用
```bash
cd ai-flow-server
mvn spring-boot:run
```

### 步骤5: 测试功能
```bash
./test-vector-search.sh
```

## 🔧 配置验证

### 1. 检查配置文件
确保以下配置存在且正确：

**application-vector.yml**:
```yaml
milvus:
  host: localhost
  port: 19530
  collection: ai_flow_documents

vector:
  storage:
    type: milvus
    milvus:
      host: localhost
      port: 19530
      collection: ai_flow_vectors
```

**application.yml**:
```yaml
spring:
  profiles:
    active: vector  # 确保激活 vector 配置
```

### 2. 检查代码配置
确保代码中的 `@Value` 注解与配置文件匹配：

```java
@Value("${milvus.host:localhost}")
private String milvusHost;

@Value("${milvus.port:19530}")
private int milvusPort;

@Value("${milvus.collection:ai_flow_documents}")
private String milvusCollection;
```

## 🚀 测试验证

### 1. API 测试
```bash
# 测试向量搜索
curl -X POST http://localhost:8081/api/vector/search \
  -H 'Content-Type: application/json' \
  -d '{
    "kbId": "kb_001",
    "query": "测试查询",
    "topK": 5,
    "scoreThreshold": 0.5
  }'

# 测试知识库列表
curl -X GET http://localhost:8081/api/kb/list
```

### 2. 功能测试
- 上传文档，验证向量化是否成功
- 执行向量搜索，验证结果
- 检查应用日志，确认没有错误

## ⚠️ 常见问题

### 1. 端口被占用
```bash
# 检查端口占用
netstat -tlnp | grep 19530

# 如果被占用，找到进程并停止
lsof -i :19530
kill -9 <PID>
```

### 2. 权限问题
```bash
# 确保脚本有执行权限
chmod +x *.sh

# 检查 Milvus 数据目录权限
ls -la /var/lib/milvus/
```

### 3. 内存不足
```bash
# 检查系统内存
free -h

# 如果内存不足，考虑增加 swap 或调整 Milvus 配置
```

## 🎯 预期结果

修复成功后：
1. ✅ Milvus 服务正常运行
2. ✅ 应用成功连接到 Milvus
3. ✅ 向量搜索API 正常工作
4. ✅ 文档向量化成功
5. ✅ 支持多种模型类型共存

## 🔮 后续优化

1. **健康检查**: 添加 Milvus 连接健康检查
2. **自动重连**: 实现连接失败时的自动重连机制
3. **配置验证**: 启动时验证所有必要的配置
4. **监控告警**: 添加向量存储状态的监控和告警

## 📝 总结

通过以下步骤解决了"未找到可用的向量存储"错误：

1. **配置修复**: 补充了缺失的 Milvus 配置
2. **命名优化**: 改进了集合命名策略，避免维度冲突
3. **工具完善**: 创建了诊断、测试和清理工具
4. **文档完善**: 提供了完整的问题解决指南

现在系统应该能够正常使用 All-MiniLM-L6-v2 作为默认嵌入模型，并成功连接到 Milvus 向量存储！ 