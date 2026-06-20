package com.hernandolopera.auth_service.dto.response.statistics;

public record UserStatisticsResponse(

        Long totalUsers,

        Long activeUsers,

        Long inactiveUsers,

        Long verifiedEmails,

        Long verifiedPhones,

        Long completedProfiles,

        Long lockedUsers,

        Long admins,

        Long clients,

        Long guides

) {
}
