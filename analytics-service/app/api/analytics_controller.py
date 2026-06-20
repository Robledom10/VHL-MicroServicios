from fastapi import APIRouter

from app.clients.auth_client import AuthClient
from app.clients.reservation_client import ReservationClient

router = APIRouter(
    prefix="/api/analytics",
    tags=["Analytics"]
)

auth_client = AuthClient()
reservation_client = ReservationClient()


@router.get("/auth-statistics")
async def auth_statistics():

    return await auth_client.get_user_statistics()


@router.get("/reservation-statistics")
async def reservation_statistics():

    return await reservation_client.get_statistics()