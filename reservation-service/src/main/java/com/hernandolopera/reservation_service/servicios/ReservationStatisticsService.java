package com.hernandolopera.reservation_service.servicios;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hernandolopera.reservation_service.dto.PackageReservationsByYearDTO;
import com.hernandolopera.reservation_service.dto.ReservationStatisticsDTO;
import com.hernandolopera.reservation_service.entidades.EstadoReserva;
import com.hernandolopera.reservation_service.repositorios.RepositorioReserva;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationStatisticsService {

    private final RepositorioReserva repositorioReserva;

    public ReservationStatisticsDTO getStatistics() {

        return ReservationStatisticsDTO.builder()
                .totalReservations(repositorioReserva.count())

                .blockedReservations(
                        repositorioReserva.countByEstado(
                                EstadoReserva.BLOQUEADA))

                .pendingReservations(
                        repositorioReserva.countByEstado(
                                EstadoReserva.PENDIENTE))

                .confirmedReservations(
                        repositorioReserva.countByEstado(
                                EstadoReserva.CONFIRMADA))

                .cancelledReservations(
                        repositorioReserva.countByEstado(
                                EstadoReserva.CANCELADA))

                .completedReservations(
                        repositorioReserva.countByEstado(
                                EstadoReserva.COMPLETADA))

                .pastReservations(
                        repositorioReserva.countByEstado(
                                EstadoReserva.PASADA))

                .paidReservations(
                        repositorioReserva.countByPagoVerificado(true))

                .unpaidReservations(
                        repositorioReserva.countByPagoVerificado(false))

                .build();
    }

    public List<PackageReservationsByYearDTO> getPackagesWithMostReservationsByYear() {
        return repositorioReserva.findPackagesWithMostReservationsByYear();
    }
}