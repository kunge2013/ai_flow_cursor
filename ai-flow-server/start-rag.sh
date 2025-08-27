#!/bin/bash

# LangChain4j RAG 启动脚本
# 用于启动支持RAG功能的后端服务

echo "🚀 启动 AI Flow Server with LangChain4j RAG..."

# 检查Java版本
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt "17" ]; then
    echo "❌ 错误: 需要Java 17或更高版本，当前版本: $java_version"
    exit 1
fi

echo "✅ Java版本检查通过: $java_version"

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven，请先安装Maven"
    exit 1
fi

echo "✅ Maven检查通过"

# 检查Milvus是否运行
echo "🔍 检查Milvus服务状态..."
if ! curl -s http://localhost:9091/api/v1/health &> /dev/null; then
    echo "⚠️  警告: Milvus服务未运行，请先启动Milvus:"
    echo "   docker-compose -f docker-compose-milvus.yml up -d"
    echo ""
    read -p "是否继续启动后端服务？(y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "❌ 启动已取消"
        exit 1
    fi
else
    echo "✅ Milvus服务运行正常"
fi

# 设置环境变量
export SPRING_PROFILES_ACTIVE=rag
export SPRING_CONFIG_ADDITIONAL_LOCATION=classpath:application-rag.yml

echo "📝 使用配置文件: application-rag.yml"
echo "🔧 激活Profile: rag"

# 编译项目
echo "🔨 编译项目..."
if ! mvn clean compile -q; then
    echo "❌ 编译失败"
    exit 1
fi

echo "✅ 编译成功"

# 启动服务
echo "🚀 启动后端服务..."
echo "📍 服务地址: http://localhost:8080"
echo "📚 API文档: http://localhost:8080/swagger-ui.html"
echo "🔍 健康检查: http://localhost:8080/actuator/health"
echo ""
echo "按 Ctrl+C 停止服务"
echo ""

# 启动Spring Boot应用
mvn spring-boot:run \
    -Dspring-boot.run.profiles=rag \
    -Dspring-boot.run.jvmArguments="-Xmx2g -Xms1g" \
    -Dspring-boot.run.arguments="--server.port=8080"
