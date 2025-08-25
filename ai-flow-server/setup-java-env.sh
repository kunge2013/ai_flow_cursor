#!/bin/bash

# Java环境配置脚本

echo "🔧 配置Java环境..."

# 设置JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# 验证配置
echo "✅ Java环境配置完成："
echo "   JAVA_HOME: $JAVA_HOME"
echo "   Java版本: $(java -version 2>&1 | head -n 1)"
echo "   Maven版本: $(mvn -version 2>&1 | head -n 1)"

# 添加到 ~/.bashrc 文件（可选）
read -p "是否要将这些环境变量添加到 ~/.bashrc 文件？(y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "" >> ~/.bashrc
    echo "# AI Flow Java环境配置" >> ~/.bashrc
    echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64" >> ~/.bashrc
    echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> ~/.bashrc
    echo "✅ 环境变量已添加到 ~/.bashrc"
    echo "   请运行 'source ~/.bashrc' 或重新打开终端以使配置生效"
fi

echo ""
echo "🚀 现在可以运行以下命令："
echo "   - 编译项目: mvn clean compile -DskipTests"
echo "   - 启动应用: ./start-with-vector.sh"
echo "   - 打包项目: mvn package -DskipTests" 