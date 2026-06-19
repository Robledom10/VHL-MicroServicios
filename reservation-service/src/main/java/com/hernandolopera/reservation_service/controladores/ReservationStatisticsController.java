package com.hernandolopera.reservation_service.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.reservation_service.dto.ReservationStatisticsDTO;
import com.hernandolopera.reservation_service.servicios.ReservationStatisticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationStatisticsController {

    private final ReservationStatisticsService reservationStatisticsService;

    @GetMapping("/api/statistics/reservations")
    public ReservationStatisticsDTO getStatistics() {

        return reservationStatisticsService.getStatistics();
    }
}