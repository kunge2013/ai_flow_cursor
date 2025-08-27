#!/bin/bash

# 清理Milvus集合脚本
# 用于解决向量维度不匹配问题

echo "🔍 开始清理Milvus集合..."

# 检查Milvus是否运行
if ! pgrep -f "milvus" > /dev/null; then
    echo "❌ Milvus 未运行，请先启动 Milvus"
    echo "   运行: ./start-milvus.sh"
    exit 1
fi

echo "✅ Milvus 正在运行"

# 连接到Milvus并列出所有集合
echo "📋 当前Milvus集合列表:"
echo "=================================="

# 使用Milvus Python客户端列出集合（如果可用）
if command -v python3 &> /dev/null; then
    python3 -c "
import sys
try:
    from pymilvus import connections, utility
    connections.connect(host='localhost', port='19530')
    collections = utility.list_collections()
    if collections:
        for col in collections:
            print(f'  - {col}')
    else:
        print('  没有找到集合')
except ImportError:
    print('  请安装 pymilvus: pip install pymilvus')
except Exception as e:
    print(f'  连接失败: {e}')
"
else
    echo "  Python3 不可用，无法自动列出集合"
fi

echo "=================================="
echo ""
echo "⚠️  注意: 以下操作将删除旧的向量集合"
echo "   这将导致之前存储的向量数据丢失"
echo ""

read -p "是否继续删除旧集合？(y/N): " -n 1 -r
echo

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🗑️  开始删除旧集合..."
    
    # 删除旧的 ai_flow_documents_* 集合
    if command -v python3 &> /dev/null; then
        python3 -c "
import sys
try:
    from pymilvus import connections, utility
    connections.connect(host='localhost', port='19530')
    
    collections = utility.list_collections()
    deleted_count = 0
    
    for col in collections:
        if col.startswith('ai_flow_documents_'):
            try:
                utility.drop_collection(col)
                print(f'  ✅ 已删除集合: {col}')
                deleted_count += 1
            except Exception as e:
                print(f'  ❌ 删除集合 {col} 失败: {e}')
    
    if deleted_count > 0:
        print(f'\n🎉 成功删除 {deleted_count} 个旧集合')
        print('   系统将在下次运行时自动创建新的正确维度集合')
    else:
        print('\nℹ️  没有找到需要删除的旧集合')
        
except ImportError:
    print('❌ 请安装 pymilvus: pip install pymilvus')
except Exception as e:
    print(f'❌ 操作失败: {e}')
"
    else
        echo "❌ Python3 不可用，无法自动删除集合"
        echo "   请手动连接到Milvus并删除以下集合:"
        echo "   - ai_flow_documents_* (所有以ai_flow_documents_开头的集合)"
    fi
else
    echo "❌ 操作已取消"
fi

echo ""
echo "💡 提示:"
echo "   1. 删除旧集合后，系统会自动创建新的正确维度集合"
echo "   2. 新集合命名格式: ai_flow_documents_{kbId}_{modelType}"
echo "   3. 例如: ai_flow_documents_kb123_minilm (384维)"
echo "   4. 或者: ai_flow_documents_kb123_openai (1536维)"
echo ""
echo "🔧 如果仍有问题，请检查:"
echo "   - Milvus 连接状态"
echo "   - 知识库配置中的 vectorModel 字段"
echo "   - 应用日志中的错误信息" 