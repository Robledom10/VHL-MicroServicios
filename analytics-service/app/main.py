from fastapi import FastAPI

from app.api.analytics_controller import router

app = FastAPI(
    title="Analytics Service",
    version="1.0.0"
)

app.include_router(router)


@app.get("/")
def health():

    return {
        "service": "analytics-service",
        "status": "UP"
    }