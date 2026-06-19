package com.hernandolopera.reservation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationStatisticsDTO {

    private Long totalReservations;

    private Long blockedReservations;

    private Long pendingReservations;

    private Long confirmedReservations;

    private Long cancelledReservations;

    private Long completedReservations;

    private Long pastReservations;

    private Long paidReservations;

    private Long unpaidReservations;
}