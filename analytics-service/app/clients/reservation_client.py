import httpx

from app.core.config import settings


class ReservationClient:

    async def get_reservations_count(self):

        async with httpx.AsyncClient() as client:

            response = await client.get(
                f"{settings.RESERVATION_SERVICE_URL}/api/reservations/count"
            )

            response.raise_for_status()

            return response.json()