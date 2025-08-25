# AI知识库向量化功能 - 快速启动指南

## 🚀 5分钟快速启动

### 1. 环境准备
```bash
# 确保Java 17已安装
java -version

# 设置JAVA_HOME（如果未设置）
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

### 2. 一键配置
```bash
# 运行配置脚本
./setup-java-env.sh

# 或手动配置
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

### 3. 构建项目
```bash
# 编译项目
mvn clean compile -DskipTests

# 打包项目
mvn package -DskipTests
```

### 4. 配置API密钥
```bash
# 设置智普AI API密钥
export ZHIPU_AI_API_KEY="your-actual-api-key"

# 可选：设置其他API密钥
export OPENAI_API_KEY="your-openai-api-key"
export QIANFAN_API_KEY="your-qianfan-api-key"
```

### 5. 启动应用
```bash
# 方式1：使用启动脚本
./start-with-vector.sh

# 方式2：使用演示脚本
./demo-vector-features.sh

# 方式3：直接启动
mvn spring-boot:run
```

## 📱 功能测试

### 1. 访问应用
- **前端界面**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **知识库管理**: http://localhost:8080/kb

### 2. 快速测试
```bash
# 测试向量化API
curl -X POST http://localhost:8080/api/vector/embed \
  -H "Content-Type: application/json" \
  -d '{
    "content": "人工智能是计算机科学的一个分支",
    "vectorModel": "zhipu"
  }'
```

### 3. 完整测试流程
1. 创建知识库（选择向量模型：zhipu）
2. 上传文档（文件/手动录入/文档库）
3. 等待向量化完成
4. 使用"命中测试"功能搜索

## 🔧 常见问题

### Q: JAVA_HOME未设置
**A**: 运行 `./setup-java-env.sh` 或手动设置：
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

### Q: 编译失败
**A**: 检查Java版本和Maven配置：
```bash
java -version
mvn -version
mvn clean compile -DskipTests
```

### Q: API调用失败
**A**: 检查API密钥配置：
```bash
echo $ZHIPU_AI_API_KEY
export ZHIPU_AI_API_KEY="your-actual-api-key"
```

### Q: 应用启动失败
**A**: 检查端口占用和配置文件：
```bash
netstat -tlnp | grep 8080
cat application-vector.yml
```

## 📚 详细文档

- **完整说明**: [README-VECTOR-INTEGRATION.md](README-VECTOR-INTEGRATION.md)
- **API测试**: [test-vector-api.http](test-vector-api.http)
- **配置文件**: [application-vector.yml](src/main/resources/application-vector.yml)

## 🎯 下一步

1. **配置真实API密钥**
2. **测试文档上传功能**
3. **体验向量搜索**
4. **集成向量数据库**
5. **优化搜索性能**

## 💡 提示

- 首次启动可能需要下载依赖，请耐心等待
- 建议先使用模拟向量测试功能，再配置真实API
- 生产环境建议配置真实的向量数据库
- 定期检查日志文件了解系统运行状态

---

**🎉 恭喜！您已成功启动AI知识库向量化功能！** 