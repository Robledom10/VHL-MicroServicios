from fastapi import APIRouter

from app.clients.auth_client import AuthClient

router = APIRouter(
    prefix="/api/analytics",
    tags=["Analytics"]
)

auth_client = AuthClient()


@router.get("/auth-statistics")
async def auth_statistics():

    return await auth_client.get_user_statistics()