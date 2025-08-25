#!/bin/bash

# AI知识库向量化功能演示脚本

echo "🌟 AI知识库向量化功能演示"
echo "================================"

# 检查Java环境
echo "📋 检查Java环境..."
if [ -z "$JAVA_HOME" ]; then
    echo "⚠️  JAVA_HOME未设置，正在配置..."
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
    export PATH=$JAVA_HOME/bin:$PATH
    echo "✅ Java环境已配置"
else
    echo "✅ Java环境已配置: $JAVA_HOME"
fi

# 检查项目状态
echo ""
echo "🔍 检查项目状态..."
if [ -f "target/ai-flow-server-0.0.1-SNAASPHOT.jar" ]; then
    echo "✅ 项目已构建，JAR文件存在"
else
    echo "📦 项目未构建，正在构建..."
    mvn clean package -DskipTests
    if [ $? -eq 0 ]; then
        echo "✅ 项目构建成功"
    else
        echo "❌ 项目构建失败"
        exit 1
    fi
fi

# 显示功能特性
echo ""
echo "🚀 向量化功能特性："
echo "   ✨ 支持多种文档上传方式"
echo "   ✨ 自动向量化处理"
echo "   ✨ 智能语义搜索"
echo "   ✨ 多种向量模型支持"
echo "   ✨ 容错和回退机制"

# 显示配置信息
echo ""
echo "⚙️  配置信息："
echo "   - 向量模型: 智普AI (text-embedding-v2)"
echo "   - 向量维度: 1024"
echo "   - 默认模型: zhipu"
echo "   - 配置文件: application-vector.yml"

# 显示API接口
echo ""
echo "🔌 可用API接口："
echo "   - POST /api/vector/embed          - 文档向量化"
echo "   - POST /api/vector/search         - 向量搜索"
echo "   - POST /api/vector/batch-embed   - 批量向量化"
echo "   - POST /api/kb                    - 创建知识库"
echo "   - POST /api/kb/{id}/documents     - 上传文档"
echo "   - POST /api/kb/{id}/test          - 命中测试"

# 显示使用方法
echo ""
echo "📖 使用方法："
echo "   1. 设置API密钥："
echo "      export ZHIPU_AI_API_KEY='your-api-key'"
echo ""
echo "   2. 启动应用："
echo "      ./start-with-vector.sh"
echo ""
echo "   3. 测试API："
echo "      - 使用 test-vector-api.http 文件"
echo "      - 或使用 Postman 等工具"
echo ""
echo "   4. 访问前端："
echo "      - 知识库管理: http://localhost:8080/kb"
echo "      - API文档: http://localhost:8080/swagger-ui.html"

# 显示测试数据
echo ""
echo "🧪 测试数据示例："
echo "   知识库名称: AI技术文档库"
echo "   向量模型: zhipu"
echo "   文档内容: 机器学习是人工智能的重要分支..."
echo "   查询示例: 什么是机器学习？"

# 显示启动选项
echo ""
echo "🎯 启动选项："
echo "   [1] 启动应用 (./start-with-vector.sh)"
echo "   [2] 查看配置 (cat application-vector.yml)"
echo "   [3] 查看API测试文件 (cat test-vector-api.http)"
echo "   [4] 查看README文档 (cat README-VECTOR-INTEGRATION.md)"
echo "   [5] 退出"

read -p "请选择操作 (1-5): " choice

case $choice in
    1)
        echo "🚀 启动应用..."
        ./start-with-vector.sh
        ;;
    2)
        echo "📁 显示配置文件..."
        cat application-vector.yml
        ;;
    3)
        echo "🔌 显示API测试文件..."
        cat test-vector-api.http
        ;;
    4)
        echo "📖 显示README文档..."
        cat README-VECTOR-INTEGRATION.md
        ;;
    5)
        echo "👋 再见！"
        exit 0
        ;;
    *)
        echo "❌ 无效选择"
        exit 1
        ;;
esac 