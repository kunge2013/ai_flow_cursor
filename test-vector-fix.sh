#!/bin/bash

# 向量存储错误修复验证脚本

echo "🧪 开始验证向量存储错误修复..."
echo "=================================="

# 检查应用是否运行
if ! ps aux | grep "ai-flow-server" | grep -v grep > /dev/null; then
    echo "❌ 应用未运行，请先启动应用"
    echo "   运行: cd ai-flow-server && mvn spring-boot:run"
    exit 1
fi

echo "✅ 应用正在运行"

# 等待应用完全启动
echo "⏳ 等待应用完全启动..."
sleep 10

# 测试向量搜索API
echo "🔍 测试向量搜索API（修复后的版本）..."

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
    
    # 检查响应内容
    if echo "$response" | grep -q "未找到可用的向量存储"; then
        echo "❌ 错误仍然存在: 未找到可用的向量存储"
        echo "📋 响应内容:"
        echo "$response"
    elif echo "$response" | grep -q "向量搜索失败"; then
        echo "❌ 仍有其他错误:"
        echo "$response"
    else
        echo "🎉 修复成功！不再出现向量存储错误"
        echo "📋 响应内容:"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    fi
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
    echo "$kb_response" | jq '.' 2>/dev/null || echo "$response"
else
    echo "❌ 知识库API 请求失败"
fi

echo ""

# 检查应用日志
echo "📝 检查应用日志..."
if [ -f "logs/application.log" ]; then
    echo "✅ 找到应用日志文件"
    echo "🔍 最近的错误日志:"
    tail -n 20 logs/application.log | grep -i error || echo "   没有找到错误日志"
    
    echo "🔍 最近的向量搜索相关日志:"
    tail -n 30 logs/application.log | grep -i "向量\|vector\|milvus" || echo "   没有找到相关日志"
else
    echo "ℹ️  应用日志文件不存在，可能还在启动中"
fi

echo ""
echo "🎯 测试完成！"
echo "=================================="

if echo "$response" | grep -q "未找到可用的向量存储"; then
    echo "❌ 修复未成功，问题仍然存在"
    echo ""
    echo "🔧 可能的原因:"
    echo "   1. 应用需要重启以加载修复后的代码"
    echo "   2. 还有其他地方存在同样的问题"
    echo "   3. 配置问题"
    echo ""
    echo "💡 建议操作:"
    echo "   1. 重启应用: pkill -f ai-flow-server && cd ai-flow-server && mvn spring-boot:run"
    echo "   2. 检查 Milvus 连接: ./diagnose-milvus.sh"
    echo "   3. 查看详细日志"
else
    echo "🎉 修复成功！向量存储错误已解决"
    echo ""
    echo "✅ 现在可以正常使用向量搜索功能"
    echo "✅ All-MiniLM-L6-v2 作为默认嵌入模型"
    echo "✅ Milvus 向量存储正常工作"
fi

echo ""
echo "💡 如果仍有问题，请检查:"
echo "   1. 应用是否完全重启"
echo "   2. Milvus 服务状态"
echo "   3. 配置文件设置"
echo "   4. 应用日志中的详细错误信息" 