# Java 17 环境变量设置说明

## 问题描述
如果你的系统默认使用Java 8，但项目需要Java 17，会出现以下错误：
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) on project ai-flow-server: Fatal error compiling: 无效的标记: --release -> [Help 1]
```

## 解决方案
使用提供的批处理文件或PowerShell脚本来临时设置Java 17环境变量。

## 使用方法

### 方法1：使用批处理文件（推荐）
1. 双击运行 `setup-java17-advanced.bat`
2. 脚本会自动检测Java 17是否已安装
3. 设置完成后，在当前命令行窗口运行Maven命令

### 方法2：使用PowerShell脚本
1. 右键 `setup-java17.ps1` → "使用PowerShell运行"
2. 或者在PowerShell中运行：`.\setup-java17.ps1`

### 方法3：使用简单批处理文件
1. 双击运行 `setup-java17.bat`
2. 适合快速设置

## 注意事项

### ⚠️ 重要提醒
- **临时设置**：这些脚本设置的环境变量仅在当前命令行窗口有效
- **重启失效**：关闭窗口后需要重新运行脚本
- **路径要求**：脚本假设Java 17安装在 `D:\soft\Java\jdk17`

### 🔧 自定义路径
如果你的Java 17安装在其他位置，请修改脚本中的路径：
```batch
set JAVA_HOME=你的Java17安装路径
set PATH=你的Java17安装路径\bin;%PATH%
```

## 验证设置
运行脚本后，可以通过以下命令验证：
```bash
echo %JAVA_HOME%    # 应该显示Java 17路径
java -version       # 应该显示Java 17版本
```

## 常用Maven命令
设置完Java 17后，可以运行：
```bash
mvn clean compile     # 清理并编译
mvn test             # 运行测试
mvn spring-boot:run  # 启动应用
mvn package          # 打包项目
```

## 永久解决方案
如果不想每次都运行脚本，可以：
1. 设置系统环境变量（推荐）
2. 使用IDE（如IntelliJ IDEA）的项目级JDK设置
3. 使用SDKMAN（Linux/Mac）或Chocolatey（Windows）管理Java版本

## 故障排除

### 问题1：找不到Java 17
- 检查是否已安装Java 17
- 确认安装路径是否正确
- 下载地址：https://adoptium.net/temurin/releases/?version=17

### 问题2：权限不足
- 以管理员身份运行PowerShell或命令提示符
- 检查文件是否被防病毒软件阻止

### 问题3：路径包含空格
- 如果路径包含空格，请用引号包围
- 例如：`"C:\Program Files\Java\jdk-17"`

## 支持
如果遇到问题，请检查：
1. Java 17是否正确安装
2. 脚本中的路径是否正确
3. 是否有足够的权限运行脚本 