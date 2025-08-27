# Java 环境配置指南

## 问题描述

在运行 Maven 项目时遇到以下错误：
```
The JAVA_HOME environment variable is not defined correctly
This environment variable is needed to run this program
NB: JAVA_HOME should point to a JDK not a JRE
```

## 解决方案

### 方法 1: 使用快速设置脚本（推荐）

1. 运行快速设置脚本：
```bash
source setup-java-env.sh
```

2. 验证配置：
```bash
echo $JAVA_HOME
java -version
mvn -version
```

### 方法 2: 使用高级诊断脚本

1. 运行高级诊断脚本：
```bash
./setup-java-env-advanced.sh
```

2. 该脚本会：
   - 检测系统中所有可用的 Java 版本
   - 验证 JDK 工具的完整性
   - 显示当前环境变量状态
   - 提供配置建议

### 方法 3: 手动配置

1. 设置 JAVA_HOME 环境变量：
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

2. 永久设置（添加到 ~/.bashrc）：
```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

## 验证配置

配置完成后，验证以下内容：

1. **JAVA_HOME 设置**：
```bash
echo $JAVA_HOME
# 应该显示: /usr/lib/jvm/java-17-openjdk-amd64
```

2. **Java 版本**：
```bash
java -version
# 应该显示 OpenJDK 17
```

3. **JDK 工具**：
```bash
javac -version
jar --version
```

4. **Maven 工作状态**：
```bash
mvn -version
# 应该显示 Java 17 和正确的 JAVA_HOME
```

5. **项目构建**：
```bash
mvn package
```

## 故障排除

### 常见问题

1. **JAVA_HOME 未设置**：
   - 检查 ~/.bashrc 文件是否包含正确的配置
   - 运行 `source ~/.bashrc` 重新加载配置

2. **Java 版本不匹配**：
   - 确保 JAVA_HOME 指向 JDK 而不是 JRE
   - 检查 PATH 中是否包含正确的 Java 版本

3. **权限问题**：
   - 确保脚本有执行权限：`chmod +x setup-java-env.sh`

4. **Maven 仍然失败**：
   - 检查 Maven 配置文件
   - 验证项目依赖是否正确

### 有用的命令

```bash
# 查看所有 Java 安装
ls -la /usr/lib/jvm/

# 查看当前 PATH
echo $PATH

# 查看 Java 可执行文件位置
which java
which javac

# 查看 Maven 配置
mvn help:effective-settings
```

## 系统要求

- Ubuntu 22.04 LTS 或更高版本
- OpenJDK 17 或更高版本
- Maven 3.6.3 或更高版本
- Bash shell

## 注意事项

1. 脚本会自动检测并使用系统中可用的最佳 Java 版本
2. 配置会永久保存到 ~/.bashrc 文件中
3. 重新打开终端后配置会自动生效
4. 如果系统中有多个 Java 版本，脚本会优先使用 OpenJDK 17

## 支持

如果遇到问题，请：

1. 运行 `./setup-java-env-advanced.sh` 获取详细诊断信息
2. 检查错误日志和输出信息
3. 验证系统环境变量设置
4. 确保所有必要的工具都已正确安装 