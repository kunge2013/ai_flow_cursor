@echo off
chcp 65001 >nul

REM LangChain4j RAG 启动脚本 (Windows版本)
REM 用于启动支持RAG功能的后端服务

echo 🚀 启动 AI Flow Server with LangChain4j RAG...

REM 检查Java版本
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到Java，请先安装Java 17或更高版本
    pause
    exit /b 1
)

for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
    goto :check_java_version
)

:check_java_version
echo ✅ Java版本检查通过

REM 检查Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到Maven，请先安装Maven
    pause
    exit /b 1
)

echo ✅ Maven检查通过

REM 检查Milvus是否运行
echo 🔍 检查Milvus服务状态...
curl -s http://localhost:9091/api/v1/health >nul 2>&1
if errorlevel 1 (
    echo ⚠️  警告: Milvus服务未运行，请先启动Milvus:
    echo    docker-compose -f docker-compose-milvus.yml up -d
    echo.
    set /p CONTINUE="是否继续启动后端服务？(y/N): "
    if /i not "%CONTINUE%"=="y" (
        echo ❌ 启动已取消
        pause
        exit /b 1
    )
) else (
    echo ✅ Milvus服务运行正常
)

REM 设置环境变量
set SPRING_PROFILES_ACTIVE=rag
set SPRING_CONFIG_ADDITIONAL_LOCATION=classpath:application-rag.yml

echo 📝 使用配置文件: application-rag.yml
echo 🔧 激活Profile: rag

REM 编译项目
echo 🔨 编译项目...
mvn clean compile -q
if errorlevel 1 (
    echo ❌ 编译失败
    pause
    exit /b 1
)

echo ✅ 编译成功

REM 启动服务
echo 🚀 启动后端服务...
echo 📍 服务地址: http://localhost:8080
echo 📚 API文档: http://localhost:8080/swagger-ui.html
echo 🔍 健康检查: http://localhost:8080/actuator/health
echo.
echo 按 Ctrl+C 停止服务
echo.

REM 启动Spring Boot应用
mvn spring-boot:run -Dspring-boot.run.profiles=rag -Dspring-boot.run.jvmArguments="-Xmx2g -Xms1g" -Dspring-boot.run.arguments="--server.port=8080"

pause
