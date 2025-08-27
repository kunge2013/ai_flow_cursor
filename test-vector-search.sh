#!/bin/bash

# 向量搜索功能测试脚本

echo "🧪 开始测试向量搜索功能..."
echo "=================================="

# 等待应用完全启动
echo "⏳ 等待应用启动..."
sleep 15

# 检查应用是否运行
if ! ps aux | grep "ai-flow-server" | grep -v grep > /dev/null; then
    echo "❌ 应用未运行，请先启动应用"
    exit 1
fi

echo "✅ 应用正在运行"

# 测试向量搜索API
echo "🔍 测试向量搜索API..."

# 使用 curl 测试向量搜索
response=$(curl -s -X POST "http://localhost:8081/api/vector/search" \
  -H "Content-Type: application/json" \
  -d '{
    "kbId": "kb_001",
    "query": "测试查询",
    "topK": 5,
    "scoreThreshold": 0.5
  }' 2>/dev/null)

if [ $? -eq 0 ]; then
    echo "✅ API 请求成功"
    echo "📋 响应内容:"
    echo "$response" | jq '.' 2>/dev/null || echo "$response"
else
    echo "❌ API 请求失败"
fi

echo ""

# 测试知识库列表API
echo "📚 测试知识库列表API..."
kb_response=$(curl -s -X GET "http://localhost:8081/api/kb/list" 2>/dev/null)

if [ $? -eq 0 ]; then
    echo "✅ 知识库API 请求成功"
    echo "📋 知识库列表:"
    echo "$kb_response" | jq '.' 2>/dev/null || echo "$kb_response"
else
    echo "❌ 知识库API 请求失败"
fi

echo ""

# 检查应用日志
echo "📝 检查应用日志..."
if [ -f "ai-flow-server/logs/application.log" ]; then
    echo "✅ 找到应用日志文件"
    echo "🔍 最近的错误日志:"
    tail -n 20 ai-flow-server/logs/application.log | grep -i error || echo "   没有找到错误日志"
else
    echo "ℹ️  应用日志文件不存在，可能还在启动中"
fi

echo ""
echo "🎯 测试完成！"
echo "=================================="
echo "💡 如果测试失败，请检查:"
echo "   1. 应用是否完全启动（等待约30秒）"
echo "   2. 端口 8081 是否可访问"
echo "   3. 应用日志中的错误信息"
echo "   4. Milvus 连接状态"
echo ""
echo "🔧 手动测试命令:"
echo "   curl -X POST http://localhost:8081/api/vector/search \\"
echo "     -H 'Content-Type: application/json' \\"
echo "     -d '{\"kbId\":\"kb_001\",\"query\":\"测试\",\"topK\":5}'" 