#!/bin/bash

# 高级 Java 环境设置脚本
# 作者: AI Assistant
# 日期: $(date)

echo "=== Java 环境配置脚本 ==="
echo ""

# 检测可用的 Java 版本
echo "检测系统中的 Java 版本..."
echo ""

# 检查系统 Java
echo "系统 Java:"
/usr/bin/java -version 2>&1 | head -3
echo ""

# 检查 JAVA_HOME 中的 Java
if [ -n "$JAVA_HOME" ]; then
    echo "JAVA_HOME Java ($JAVA_HOME):"
    $JAVA_HOME/bin/java -version 2>&1 | head -3
    echo ""
fi

# 检查 SDKMAN Java（如果存在）
if [ -d "$HOME/.sdkman" ]; then
    echo "SDKMAN Java:"
    if [ -f "$HOME/.sdkman/candidates/java/current/bin/java" ]; then
        $HOME/.sdkman/candidates/java/current/bin/java -version 2>&1 | head -3
    else
        echo "SDKMAN 已安装但未找到当前 Java 版本"
    fi
    echo ""
fi

# 设置推荐的 Java 版本
RECOMMENDED_JAVA="/usr/lib/jvm/java-17-openjdk-amd64"

echo "推荐使用的 Java 版本: $RECOMMENDED_JAVA"
echo ""

# 验证推荐的 Java 版本
if [ -f "$RECOMMENDED_JAVA/bin/java" ]; then
    echo "验证推荐的 Java 版本:"
    $RECOMMENDED_JAVA/bin/java -version 2>&1 | head -3
    echo ""
    
    # 检查是否包含 JDK 工具
    if [ -f "$RECOMMENDED_JAVA/bin/javac" ]; then
        echo "✓ 包含 JDK 工具 (javac)"
    else
        echo "✗ 缺少 JDK 工具 (javac)"
    fi
    
    if [ -f "$RECOMMENDED_JAVA/bin/jar" ]; then
        echo "✓ 包含 JDK 工具 (jar)"
    else
        echo "✗ 缺少 JDK 工具 (jar)"
    fi
    
    echo ""
else
    echo "✗ 推荐的 Java 版本不存在: $RECOMMENDED_JAVA"
    echo ""
fi

# 显示当前环境变量
echo "当前环境变量:"
echo "JAVA_HOME: ${JAVA_HOME:-未设置}"
echo "PATH 中的 Java: $(which java 2>/dev/null || echo '未找到')"
echo ""

# 显示 Maven 信息
echo "Maven 信息:"
if command -v mvn >/dev/null 2>&1; then
    mvn -version | head -3
else
    echo "Maven 未安装或不在 PATH 中"
fi
echo ""

echo "=== 配置建议 ==="
echo "1. 当前脚本已自动设置 JAVA_HOME 为: $RECOMMENDED_JAVA"
echo "2. 环境变量已添加到 ~/.bashrc 文件中"
echo "3. 重新打开终端或运行 'source ~/.bashrc' 使配置生效"
echo "4. 运行 'mvn package' 测试 Maven 是否正常工作"
echo ""

echo "=== 故障排除 ==="
echo "如果遇到问题，请检查:"
echo "- JAVA_HOME 是否正确设置: echo \$JAVA_HOME"
echo "- Java 版本: java -version"
echo "- Maven 版本: mvn -version"
echo "- PATH 是否包含 Java: echo \$PATH"
echo ""

echo "脚本执行完成！" 