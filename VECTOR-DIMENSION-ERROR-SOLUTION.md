# 🚨 向量维度不匹配错误解决方案

## 🔍 错误分析

### 错误信息
```
dev.langchain4j.store.embedding.milvus.RequestToMilvusFailedException: Request to Milvus DB failed. Response status:'-5'.

Caused by: io.milvus.exception.ParamException: Incorrect dimension for field 'vector': the no.0 vector's dimension: 384 is not equal to field's dimension: 1536
```

### 问题根源
1. **新模型维度**: All-MiniLM-L6-v2 输出 **384** 维向量
2. **旧集合维度**: Milvus 集合之前为 OpenAI 模型创建，维度是 **1536**
3. **维度冲突**: 384 ≠ 1536，导致插入失败

## 🛠️ 解决方案

### 方案1: 自动清理旧集合（推荐）

运行清理脚本：
```bash
./cleanup-milvus-collections.sh
```

脚本会：
- 检查 Milvus 运行状态
- 列出所有现有集合
- 删除旧的 `ai_flow_documents_*` 集合
- 让系统自动创建新的正确维度集合

### 方案2: 手动清理

#### 2.1 启动 Milvus
```bash
cd ai-flow-server
./start-milvus.sh
```

#### 2.2 连接到 Milvus 并删除旧集合

**使用 Python 客户端:**
```python
from pymilvus import connections, utility

# 连接到 Milvus
connections.connect(host='localhost', port='19530')

# 列出所有集合
collections = utility.list_collections()
print("现有集合:", collections)

# 删除旧的 ai_flow_documents_* 集合
for col in collections:
    if col.startswith('ai_flow_documents_'):
        utility.drop_collection(col)
        print(f"已删除集合: {col}")
```

**使用 Milvus 命令行工具:**
```bash
# 安装 Milvus 命令行工具
pip install pymilvus

# 连接到 Milvus
milvus-cli connect localhost:19530

# 列出集合
show collections

# 删除旧集合
drop collection ai_flow_documents_your_kb_id
```

### 方案3: 修改代码避免冲突（已实现）

代码已修改为在集合名称中包含模型类型：
- **新集合命名**: `ai_flow_documents_{kbId}_{modelType}`
- **示例**: 
  - `ai_flow_documents_kb123_minilm` (384维)
  - `ai_flow_documents_kb123_openai` (1536维)

## 🔧 预防措施

### 1. 集合命名策略
- 在集合名称中包含模型类型和维度信息
- 避免不同模型使用相同集合名称

### 2. 维度检查
- 在创建集合前检查现有集合的维度
- 如果维度不匹配，自动创建新集合

### 3. 模型配置
- 在知识库配置中明确指定 `vectorModel` 字段
- 支持的值：`sentence-transformers`、`openai` 等

## 📋 操作步骤

### 步骤1: 停止应用
```bash
# 如果应用正在运行，先停止
pkill -f "ai-flow-server"
```

### 步骤2: 清理旧集合
```bash
# 运行清理脚本
./cleanup-milvus-collections.sh

# 或者手动清理
# 连接到 Milvus 并删除旧集合
```

### 步骤3: 重启应用
```bash
cd ai-flow-server
mvn spring-boot:run
```

### 步骤4: 验证修复
- 检查应用日志，确认没有维度错误
- 尝试上传新文档，验证向量化是否成功
- 检查 Milvus 中是否创建了新的正确维度集合

## 🔍 验证方法

### 1. 检查集合创建
```python
from pymilvus import connections, utility

connections.connect(host='localhost', port='19530')
collections = utility.list_collections()

for col in collections:
    if col.startswith('ai_flow_documents_'):
        # 获取集合信息
        collection = utility.load_collection(col)
        schema = collection.schema
        print(f"集合: {col}")
        print(f"  字段: {[f.name for f in schema.fields]}")
        print(f"  向量维度: {[f.params.get('dim') for f in schema.fields if f.dtype == 100]}")
```

### 2. 检查应用日志
```bash
# 查看应用日志
tail -f ai-flow-server/logs/application.log

# 查找以下信息：
# - "创建向量存储: 集合=..., 模型=..., 维度=..."
# - 没有维度不匹配错误
```

## ⚠️ 注意事项

### 1. 数据丢失
- 删除旧集合会导致之前存储的向量数据丢失
- 需要重新处理文档以生成新的向量

### 2. 集合命名
- 新集合名称包含模型类型，避免未来冲突
- 支持同时使用多种模型类型

### 3. 性能影响
- 首次使用 All-MiniLM-L6-v2 会下载模型文件（约80MB）
- 模型加载后会占用一定内存

## 🎯 预期结果

修复成功后：
1. ✅ 不再出现向量维度不匹配错误
2. ✅ 系统自动创建正确维度的集合
3. ✅ 文档向量化成功，状态更新为 "completed"
4. ✅ 向量搜索功能正常工作
5. ✅ 支持多种模型类型共存

## 🔮 后续优化

1. **自动迁移**: 实现旧集合到新集合的数据迁移
2. **维度检测**: 自动检测并调整集合维度
3. **模型热切换**: 支持运行时切换不同的嵌入模型
4. **性能监控**: 监控向量化性能和内存使用

---

如果问题仍然存在，请检查：
- Milvus 连接状态和版本
- 应用配置文件中的模型设置
- 知识库配置中的 vectorModel 字段
- 完整的应用日志信息 