@echo off
chcp 65001 >nul
echo ========================================
echo        Java 17 环境变量设置工具
echo ========================================
echo.

REM 检查Java 17是否已安装
if not exist "D:\soft\Java\jdk17" (
    echo [错误] 未找到Java 17安装目录: D:\soft\Java\jdk17
    echo.
    echo 请先安装Java 17到该目录，或者修改此批处理文件中的路径
    echo.
    echo 推荐下载地址：
    echo   https://adoptium.net/temurin/releases/?version=17
    echo.
    pause
    exit /b 1
)

if not exist "D:\soft\Java\jdk17\bin\java.exe" (
    echo [错误] 在 D:\soft\Java\jdk17\bin\java.exe 中未找到java.exe
    echo 请检查Java 17是否正确安装
    pause
    exit /b 1
)

echo [信息] 检测到Java 17安装目录: D:\soft\Java\jdk17
echo.

REM 设置JAVA_HOME环境变量
set JAVA_HOME=D:\soft\Java\jdk17

REM 将Java 17的bin目录添加到PATH的最前面
set PATH=D:\soft\Java\jdk17\bin;%PATH%

echo [信息] 正在设置环境变量...
echo JAVA_HOME = %JAVA_HOME%
echo.

REM 验证Java版本
echo [信息] 验证Java版本：
java -version
if %errorlevel% neq 0 (
    echo [错误] Java版本验证失败
    pause
    exit /b 1
)

echo.
echo ========================================
echo [成功] Java 17环境变量设置完成！
echo ========================================
echo.
echo 现在可以运行以下Maven命令：
echo   mvn clean compile     - 清理并编译项目
echo   mvn test             - 运行测试
echo   mvn spring-boot:run  - 启动Spring Boot应用
echo   mvn package          - 打包项目
echo.
echo 注意：此设置仅在当前命令行窗口有效
echo 关闭窗口后需要重新运行此批处理文件
echo.
echo 按任意键继续...
pause >nul 