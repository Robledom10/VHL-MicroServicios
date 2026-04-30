package com.vhl.reservationservice.service.impl;

import com.vhl.reservationservice.client.PackageServiceClient;
import com.vhl.reservationservice.dto.CancelReservationRequestDTO;
import com.vhl.reservationservice.dto.CancelReservationResponseDTO;
import com.vhl.reservationservice.dto.ReservationRequestDTO;
import com.vhl.reservationservice.dto.ReservationResponseDTO;
import com.vhl.reservationservice.dto.TravelerRequestDTO;
import com.vhl.reservationservice.exception.InsufficientSpotsException;
import com.vhl.reservationservice.exception.ReservationException;
import com.vhl.reservationservice.model.Reservation;
import com.vhl.reservationservice.model.ReservationAudit;
import com.vhl.reservationservice.repository.ReservationRepository;
import com.vhl.reservationservice.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PackageServiceClient packageServiceClient;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reservationService, "maxSpotsPerReservation", 10);
    }

    @Test
    void createReservationStoresReservationAndReturnsConfirmationData() {
        ReservationRequestDTO request = new ReservationRequestDTO(20L, 7L, 3);
        request.setNotes("Ventana si es posible");
        PackageServiceClient.PackageInfo packageInfo =
                new PackageServiceClient.PackageInfo(20L, "Amazonas", 12, 5, 150.0);

        when(packageServiceClient.getPackageInfo(20L)).thenReturn(packageInfo);
        when(reservationRepository.existsByConfirmationCode(anyString())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setId(100L);
            return reservation;
        });

        ReservationResponseDTO response = reservationService.createReservation(request);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getPackageId()).isEqualTo(20L);
        assertThat(response.getUserId()).isEqualTo(7L);
        assertThat(response.getNumberOfSpots()).isEqualTo(3);
        assertThat(response.getTotalPrice()).isEqualTo(450.0);
        assertThat(response.getStatus()).isEqualTo("Pendiente");
        assertThat(response.getConfirmationCode()).matches("RES-[A-Z0-9]{6}-\\d{4}");
        assertThat(response.getNotes()).isEqualTo("Ventana si es posible");

        verify(auditService).logAction(100L, ReservationAudit.AuditAction.CREATED, "SYSTEM");
    }

    @Test
    void createReservationStoresTravelersInNewReservation() {
        ReservationRequestDTO request = new ReservationRequestDTO(20L, 7L, 2);
        TravelerRequestDTO firstTraveler = new TravelerRequestDTO();
        firstTraveler.setFullName("Laura Gomez");
        firstTraveler.setDocumentType("CC");
        firstTraveler.setDocumentNumber("100200300");
        TravelerRequestDTO secondTraveler = new TravelerRequestDTO();
        secondTraveler.setFullName("Carlos Ruiz");
        secondTraveler.setDocumentType("CC");
        secondTraveler.setDocumentNumber("400500600");
        request.setTravelers(List.of(firstTraveler, secondTraveler));
        PackageServiceClient.PackageInfo packageInfo =
                new PackageServiceClient.PackageInfo(20L, "Amazonas", 12, 5, 150.0);

        when(packageServiceClient.getPackageInfo(20L)).thenReturn(packageInfo);
        when(reservationRepository.existsByConfirmationCode(anyString())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setId(101L);
            return reservation;
        });

        ReservationResponseDTO response = reservationService.createReservation(request);

        assertThat(response.getTravelers()).hasSize(2);
        assertThat(response.getTravelers().get(0).getFullName()).isEqualTo("Laura Gomez");
        assertThat(response.getTravelers().get(1).getDocumentNumber()).isEqualTo("400500600");
    }

    @Test
    void cancelReservationSetsRefundInformation() {
        Reservation reservation = new Reservation(20L, 7L, 2, Reservation.ReservationStatus.PENDING);
        reservation.setId(102L);
        reservation.setTotalPrice(300.0);
        reservation.setConfirmationCode("RES-ABC123-2026");
        CancelReservationRequestDTO request = new CancelReservationRequestDTO();
        request.setCancellationReason("Cliente solicita devolucion");

        when(reservationRepository.findById(102L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CancelReservationResponseDTO response = reservationService.cancelReservation(102L, request);

        assertThat(response.getReservationStatus()).isEqualTo("Cancelada");
        assertThat(response.getRefundAmount()).isEqualTo(300.0);
        assertThat(response.getRefundStatus()).isEqualTo("Procesada");
        assertThat(response.getCancellationReason()).isEqualTo("Cliente solicita devolucion");
    }

    @Test
    void createReservationRejectsInvalidRequiredDataBeforeCallingPackageService() {
        ReservationRequestDTO request = new ReservationRequestDTO(20L, 0L, 3);

        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(ReservationException.class)
                .hasMessageContaining("usuario");

        verifyNoInteractions(packageServiceClient);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservationRejectsRequestWhenThereAreNotEnoughSpots() {
        ReservationRequestDTO request = new ReservationRequestDTO(20L, 7L, 3);
        PackageServiceClient.PackageInfo packageInfo =
                new PackageServiceClient.PackageInfo(20L, "Amazonas", 12, 2, 150.0);

        when(packageServiceClient.getPackageInfo(20L)).thenReturn(packageInfo);

        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(InsufficientSpotsException.class)
                .hasMessageContaining("Cupos insuficientes");

        verify(reservationRepository, never()).save(any(Reservation.class));
        verify(auditService, never()).logAction(eq(100L), eq(ReservationAudit.AuditAction.CREATED), anyString());
    }
}
