from pydantic import BaseModel


class AuthStatisticsResponse(BaseModel):

    totalUsers: int

    activeUsers: int

    inactiveUsers: int

    verifiedEmails: int

    verifiedPhones: int

    completedProfiles: int

    lockedUsers: int

    admins: int

    clients: int

    guides: int