from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.analytics_controller import router

app = FastAPI(
    title="Analytics Service",
    description="Microservicio en Python para la consolidación de estadísticas del sistema de turismo",
    version="1.0.0"
)

# ─── CONFIGURACIÓN DE CORS (Soluciona el Socket Hang Up con el Gateway) ───
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], # Permite peticiones desde el Gateway y Frontend
    allow_credentials=True,
    allow_methods=["*"], # Permite GET, POST, OPTIONS, etc.
    allow_headers=["*"], # Permite pasar el Bearer Token JWT sin romper el socket
)

# Incluimos las rutas asegurando que mantengan la estructura limpia
app.include_router(router)


@app.get("/")
def health():
    return {
        "service": "analytics-service",
        "status": "UP",
        "description": "Consumiendo APIs transaccionales en caliente"
    }