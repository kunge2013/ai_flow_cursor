#!/bin/bash

# Milvus 诊断脚本
# 用于诊断向量存储连接问题

echo "🔍 开始诊断 Milvus 连接问题..."
echo "=================================="

# 1. 检查 Milvus 进程
echo "1️⃣ 检查 Milvus 进程状态:"
if pgrep -f "milvus" > /dev/null; then
    echo "   ✅ Milvus 进程正在运行"
    ps aux | grep milvus | grep -v grep
else
    echo "   ❌ Milvus 进程未运行"
fi
echo ""

# 2. 检查端口
echo "2️⃣ 检查 Milvus 端口 (19530):"
if netstat -tlnp 2>/dev/null | grep 19530 > /dev/null; then
    echo "   ✅ 端口 19530 正在监听"
    netstat -tlnp | grep 19530
else
    echo "   ❌ 端口 19530 未监听"
fi
echo ""

# 3. 检查 Docker 容器（如果使用 Docker）
echo "3️⃣ 检查 Docker 容器状态:"
if command -v docker &> /dev/null; then
    if docker ps | grep milvus > /dev/null; then
        echo "   ✅ Milvus Docker 容器正在运行"
        docker ps | grep milvus
    else
        echo "   ❌ Milvus Docker 容器未运行"
        echo "   查看所有容器:"
        docker ps -a | grep milvus
    fi
else
    echo "   ℹ️  Docker 未安装或不可用"
fi
echo ""

# 4. 测试网络连接
echo "4️⃣ 测试网络连接:"
if nc -z localhost 19530 2>/dev/null; then
    echo "   ✅ 可以连接到 localhost:19530"
else
    echo "   ❌ 无法连接到 localhost:19530"
fi
echo ""

# 5. 检查配置文件
echo "5️⃣ 检查配置文件:"
echo "   主配置文件: application.yml"
if grep -q "milvus" ai-flow-server/src/main/resources/application.yml; then
    echo "   ✅ 主配置文件包含 Milvus 配置"
else
    echo "   ❌ 主配置文件缺少 Milvus 配置"
fi

echo "   向量配置文件: application-vector.yml"
if grep -q "milvus" ai-flow-server/src/main/resources/application-vector.yml; then
    echo "   ✅ 向量配置文件包含 Milvus 配置"
else
    echo "   ❌ 向量配置文件缺少 Milvus 配置"
fi
echo ""

# 6. 尝试 Python 连接测试
echo "6️⃣ Python 连接测试:"
if command -v python3 &> /dev/null; then
    python3 -c "
import sys
try:
    from pymilvus import connections
    connections.connect(host='localhost', port='19530', timeout=5)
    print('   ✅ Python 连接成功')
    
    # 列出集合
    from pymilvus import utility
    collections = utility.list_collections()
    print(f'   📋 现有集合数量: {len(collections)}')
    if collections:
        for col in collections:
            print(f'      - {col}')
    
except ImportError:
    print('   ❌ 缺少 pymilvus 库，请安装: pip install pymilvus')
except Exception as e:
    print(f'   ❌ Python 连接失败: {e}')
"
else
    echo "   ❌ Python3 不可用"
fi
echo ""

# 7. 提供解决方案
echo "7️⃣ 问题诊断结果:"
echo "=================================="

if pgrep -f "milvus" > /dev/null && nc -z localhost 19530 2>/dev/null; then
    echo "🎉 Milvus 服务正常运行！"
    echo "   如果仍有问题，请检查应用配置和日志"
else
    echo "🚨 发现 Milvus 问题！"
    echo ""
    echo "🔧 解决方案:"
    echo "   1. 启动 Milvus 服务:"
    echo "      cd ai-flow-server && ./start-milvus.sh"
    echo ""
    echo "   2. 或者使用 Docker:"
    echo "      docker-compose -f docker-compose-milvus.yml up -d"
    echo ""
    echo "   3. 检查配置文件中的 Milvus 设置"
    echo ""
    echo "   4. 重启应用服务"
fi

echo ""
echo "💡 提示:"
echo "   - 确保 Milvus 服务在应用启动前运行"
echo "   - 检查防火墙设置，确保端口 19530 可访问"
echo "   - 查看应用日志获取详细错误信息"
echo "   - 如果使用 Docker，确保容器网络配置正确" 