from fastapi import APIRouter

from app.clients.auth_client import AuthClient
from app.clients.reservation_client import ReservationClient
from app.clients.catalog_client import CatalogClient

router = APIRouter(
    prefix="/api/analytics",
    tags=["Analytics"]
)

auth_client = AuthClient()
reservation_client = ReservationClient()
catalog_client = CatalogClient()


@router.get("/auth-statistics")
async def auth_statistics():

    return await auth_client.get_user_statistics()


@router.get("/reservation-statistics")
async def reservation_statistics():

    return await reservation_client.get_statistics()

@router.get("/catalog-statistics")
async def catalog_statistics():

    return await catalog_client.get_catalog_statistics()


@router.get("/packages-by-year")
async def packages_by_year():

    return await reservation_client.get_packages_by_year()