#!/bin/bash

echo "🛑 停止 Milvus Lite 向量数据库..."

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker未运行"
    exit 1
fi

# 停止所有Milvus相关服务
echo "⏹️  停止完整版服务..."
docker-compose -f docker-compose-milvus.yml down 2>/dev/null

echo "⏹️  停止简化版服务..."
docker-compose -f docker-compose-milvus-lite-simple.yml down 2>/dev/null

# 清理未使用的容器和网络
echo "🧹 清理未使用的资源..."
docker container prune -f
docker network prune -f

echo ""
echo "✅ Milvus Lite 已停止！"
echo ""
echo "💾 数据卷已保留，重新启动时会恢复数据"
echo "🗑️  如需完全清理，请运行: docker volume prune" 