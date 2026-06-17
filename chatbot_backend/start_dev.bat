@echo off
echo ==========================================
echo   Sharky Chatbot - Modo Desarrollo
echo   Puerto: 8090 / Ollama: localhost:11434
echo ==========================================
echo.
cd /d "%~dp0"
set PYTHONUTF8=1
set OLLAMA_URL=http://localhost:11434
set OLLAMA_MODEL=qwen3:1.7b
uvicorn main:app --host 0.0.0.0 --port 8090 --reload
pause
