package com.hernandolopera.reservation_service.controladores;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.reservation_service.dto.PackageReservationsByYearDTO;
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

    @GetMapping("/api/statistics/packages-by-year")
    public List<PackageReservationsByYearDTO> getPackagesByYear() {

        return reservationStatisticsService.getPackagesWithMostReservationsByYear();
    }
}