#!/bin/bash

echo "🚀 启动 Milvus Lite 向量数据库..."

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker未运行，请先启动Docker服务"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

# 选择配置文件
echo "请选择启动模式："
echo "1) 完整版 (包含etcd、MinIO、Redis等依赖服务)"
echo "2) 简化版 (仅Milvus Lite)"
read -p "请输入选择 (1 或 2): " choice

case $choice in
    1)
        echo "📦 启动完整版 Milvus..."
        docker-compose -f docker-compose-milvus.yml up -d
        ;;
    2)
        echo "⚡ 启动简化版 Milvus Lite..."
        docker-compose -f docker-compose-milvus-lite-simple.yml up -d
        ;;
    *)
        echo "❌ 无效选择，使用简化版启动"
        docker-compose -f docker-compose-milvus-lite-simple.yml up -d
        ;;
esac

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 10

# 检查服务状态
echo "🔍 检查服务状态..."
docker-compose ps

echo ""
echo "✅ Milvus Lite 启动完成！"
echo ""
echo "📊 服务访问地址："
echo "   - Milvus gRPC: localhost:19530"
echo "   - Milvus HTTP: http://localhost:9091"
echo "   - 健康检查: http://localhost:9091/healthz"
echo ""
echo "🔧 常用命令："
echo "   - 查看日志: docker-compose logs -f milvus-lite"
echo "   - 停止服务: docker-compose down"
echo "   - 重启服务: docker-compose restart"
echo ""
echo "📚 更多信息请查看 README-MILVUS.md" 