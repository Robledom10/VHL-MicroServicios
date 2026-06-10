import httpx

from app.core.config import settings


class AuthClient:

    async def get_user_statistics(self):

        async with httpx.AsyncClient() as client:

            response = await client.get(
                f"{settings.AUTH_SERVICE_URL}/api/statistics/users"
            )

            response.raise_for_status()

            return response.json()