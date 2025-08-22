# 设置Java 17环境变量
Write-Host "正在设置Java 17环境变量..." -ForegroundColor Green

# 设置JAVA_HOME环境变量
$env:JAVA_HOME = "D:\soft\Java\jdk17"

# 将Java 17的bin目录添加到PATH的最前面
$env:PATH = "D:\soft\Java\jdk17\bin;" + $env:PATH

# 验证Java版本
Write-Host "`n当前Java版本：" -ForegroundColor Yellow
java -version

Write-Host "`nJava 17环境变量设置完成！" -ForegroundColor Green
Write-Host "JAVA_HOME = $env:JAVA_HOME" -ForegroundColor Cyan

Write-Host "`n现在可以运行Maven命令了，例如：" -ForegroundColor Yellow
Write-Host "  mvn clean compile" -ForegroundColor White
Write-Host "  mvn spring-boot:run" -ForegroundColor White

Write-Host "`n注意：此设置仅在当前PowerShell会话有效" -ForegroundColor Red
Write-Host "关闭窗口后需要重新运行此脚本" -ForegroundColor Red

Write-Host "`n按任意键继续..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown") 