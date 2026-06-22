import httpx

from app.core.config import settings


class AuthClient:

    async def get_user_statistics(self):

        async with httpx.AsyncClient() as client:

            response = await client.get(
                f"{settings.AUTH_SERVICE_URL}/api/statistics/users"
            )

            print("STATUS:", response.status_code)
            print("BODY:", response.text)

            response.raise_for_status()

            return response.json()