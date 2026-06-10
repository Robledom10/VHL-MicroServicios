import httpx

from app.core.config import settings


class FinancialClient:

    async def get_total_revenue(self):

        async with httpx.AsyncClient() as client:

            response = await client.get(
                f"{settings.FINANCIAL_SERVICE_URL}/api/payments/revenue"
            )

            response.raise_for_status()

            return response.json()