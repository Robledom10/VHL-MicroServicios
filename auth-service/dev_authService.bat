@echo off
echo 🔥 Liberando puerto 8080...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
  taskkill /PID %%a /F
)

echo 🚀 Iniciando Spring Boot en modo DEV...

mvn clean spring-boot:run "-Dspring-boot.run.profiles=dev"

pause