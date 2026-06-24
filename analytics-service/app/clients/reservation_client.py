import httpx

from app.core.config import settings


class ReservationClient:

    async def get_statistics(self):

        async with httpx.AsyncClient() as client:

            response = await client.get(
                f"{settings.RESERVATION_SERVICE_URL}/api/statistics/reservations"
            )

            print("STATUS:", response.status_code)
            print("BODY:", response.text)

            response.raise_for_status()

            return response.json()

    async def get_packages_by_year(self):

        async with httpx.AsyncClient() as client:

            response = await client.get(
                f"{settings.RESERVATION_SERVICE_URL}/api/statistics/packages-by-year"
            )

            response.raise_for_status()

            return response.json()