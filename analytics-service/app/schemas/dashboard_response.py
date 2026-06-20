from pydantic import BaseModel


class DashboardResponse(BaseModel):

    total_users: int

    total_reservations: int

    total_revenue: float

    total_packages: int

    total_documents: int

    total_images: int