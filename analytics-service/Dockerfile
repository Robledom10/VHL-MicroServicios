# Imagen base
FROM python:3.12-slim

# Evita generar archivos .pyc
ENV PYTHONDONTWRITEBYTECODE=1

# Muestra logs inmediatamente
ENV PYTHONUNBUFFERED=1

# Directorio de trabajo
WORKDIR /app

# Instalar dependencias
COPY requirements.txt .

RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# Copiar código fuente
COPY . .

# Exponer puerto del Analytics Service
EXPOSE 8088

# Ejecutar FastAPI
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8088"]