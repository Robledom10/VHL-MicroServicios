@echo off
echo 🔥 Liberando puerto 8081...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
  taskkill /PID %%a /F
)

echo 🚀 Iniciando Spring Boot en modo DEVLOCAL...

mvn clean spring-boot:run "-Dspring-boot.run.profiles=devLocal"

pause