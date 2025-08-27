#!/bin/bash

# 设置 JAVA_HOME 环境变量
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# 将 Java 可执行文件路径添加到 PATH
export PATH=$JAVA_HOME/bin:$PATH

# 验证设置
echo "JAVA_HOME 已设置为: $JAVA_HOME"
echo "Java 版本:"
java -version
echo ""
echo "Javac 版本:"
javac -version
echo ""
echo "Maven 版本:"
mvn -version

echo ""
echo "要永久设置这些环境变量，请将以下内容添加到您的 ~/.bashrc 文件中:"
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
echo "export PATH=\$JAVA_HOME/bin:\$PATH" 