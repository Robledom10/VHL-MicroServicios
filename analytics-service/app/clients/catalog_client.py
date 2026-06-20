import httpx

from app.core.config import settings


class CatalogClient:

    async def get_catalog_statistics(self):

        async with httpx.AsyncClient() as client:

            response = await client.get(
                f"{settings.TOURIST_CATALOG_SERVICE_URL}/api/statistics/catalog"
            )

            response.raise_for_status()

            return response.json()