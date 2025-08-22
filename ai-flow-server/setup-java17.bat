@echo off
echo 正在设置Java 17环境变量...

REM 设置JAVA_HOME环境变量
set JAVA_HOME=D:\soft\Java\jdk17

REM 将Java 17的bin目录添加到PATH的最前面
set PATH=D:\soft\Java\jdk17\bin;%PATH%

REM 验证Java版本
echo.
echo 当前Java版本：
java -version

echo.
echo Java 17环境变量设置完成！
echo JAVA_HOME = %JAVA_HOME%
echo.
echo 现在可以运行Maven命令了，例如：
echo   mvn clean compile
echo   mvn spring-boot:run
echo.
echo 注意：此设置仅在当前命令行窗口有效
echo 关闭窗口后需要重新运行此批处理文件
echo.
pause 