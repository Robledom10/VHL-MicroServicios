from asyncio import gather

from app.clients.auth_client import AuthClient
from app.clients.reservation_client import ReservationClient
from app.clients.financial_client import FinancialClient

from app.schemas.dashboard_response import (
    DashboardResponse
)


class AnalyticsService:

    def __init__(self):

        self.auth_client = AuthClient()

        self.reservation_client = ReservationClient()

        self.financial_client = FinancialClient()

    async def dashboard(self):

        users, reservations, revenue = await gather(
            self.auth_client.get_users_count(),
            self.reservation_client.get_reservations_count(),
            self.financial_client.get_total_revenue()
        )

        return DashboardResponse(
            total_users=users,
            total_reservations=reservations,
            total_revenue=revenue,
            total_packages=0,
            total_documents=0,
            total_images=0
        )