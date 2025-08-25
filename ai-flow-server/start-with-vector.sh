#!/bin/bash

# AI Flow Server 启动脚本（包含向量化功能）

echo "🚀 启动 AI Flow Server（向量化功能已启用）"

# 检查Java版本
echo "📋 检查Java版本..."
java -version

# 设置环境变量（请根据实际情况修改）
echo "🔑 设置环境变量..."
export ZHIPU_AI_API_KEY="your-zhipu-ai-api-key-here"
export OPENAI_API_KEY="your-openai-api-key-here"
export QIANFAN_API_KEY="your-qianfan-api-key-here"

echo "⚠️  请确保已设置正确的API密钥！"
echo "   当前设置："
echo "   - ZHIPU_AI_API_KEY: ${ZHIPU_AI_API_KEY}"
echo "   - OPENAI_API_KEY: ${OPENAI_API_KEY}"
echo "   - QIANFAN_API_KEY: ${QIANFAN_API_KEY}"

# 检查配置文件
echo "📁 检查配置文件..."
if [ -f "src/main/resources/application-vector.yml" ]; then
    echo "✅ 向量配置文件存在"
else
    echo "❌ 向量配置文件不存在"
    exit 1
fi

# 编译项目
echo "🔨 编译项目..."
mvn clean compile -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ 编译成功"
else
    echo "❌ 编译失败"
    exit 1
fi

# 启动应用
echo "🌟 启动应用..."
echo "📖 应用启动后，可以访问以下功能："
echo "   - 知识库管理: http://localhost:8080/kb"
echo "   - 向量搜索API: http://localhost:8080/api/vector"
echo "   - API文档: http://localhost:8080/swagger-ui.html"

mvn spring-boot:run 