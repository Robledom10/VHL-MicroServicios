from fastapi import FastAPI
from app.api.analytics_controller import router

app = FastAPI(
    title="Analytics Service",
    description="Microservicio en Python para la consolidación de estadísticas del sistema de turismo",
    version="1.0.0"
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