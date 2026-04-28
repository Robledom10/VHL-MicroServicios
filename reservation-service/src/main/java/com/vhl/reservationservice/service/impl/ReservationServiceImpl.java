package com.vhl.reservationservice.service.impl;

import com.vhl.reservationservice.client.PackageServiceClient;
import com.vhl.reservationservice.dto.ReservationRequestDTO;
import com.vhl.reservationservice.dto.ReservationResponseDTO;
import com.vhl.reservationservice.exception.InsufficientSpotsException;
import com.vhl.reservationservice.exception.PackageNotFoundException;
import com.vhl.reservationservice.exception.ReservationException;
import com.vhl.reservationservice.model.Reservation;
import com.vhl.reservationservice.repository.ReservationRepository;
import com.vhl.reservationservice.service.AuditService;
import com.vhl.reservationservice.service.ReservationService;
import com.vhl.reservationservice.util.ConfirmationCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PackageServiceClient packageServiceClient;

    @Autowired
    private AuditService auditService;

    @Value("${reservation.max-spots-per-reservation:10}")
    private Integer maxSpotsPerReservation;

    @Override
    public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO) {
        logger.info("Iniciando creación de reserva para usuario: {}, paquete: {}", 
                    requestDTO.getUserId(), requestDTO.getPackageId());

        // Validaciones básicas
        validateReservationRequest(requestDTO);

        // Verificar que el paquete existe
        PackageServiceClient.PackageInfo packageInfo = packageServiceClient.getPackageInfo(requestDTO.getPackageId());
        if (packageInfo == null) {
            logger.error("Paquete no encontrado: {}", requestDTO.getPackageId());
            throw new PackageNotFoundException("Paquete no encontrado con ID: " + requestDTO.getPackageId());
        }

        // Validar cupos disponibles
        if (packageInfo.getAvailableSpots() < requestDTO.getNumberOfSpots()) {
            logger.error("Cupos insuficientes. Disponibles: {}, Solicitados: {}", 
                        packageInfo.getAvailableSpots(), requestDTO.getNumberOfSpots());
            throw new InsufficientSpotsException(
                    String.format("Cupos insuficientes. Disponibles: %d, Solicitados: %d",
                            packageInfo.getAvailableSpots(), requestDTO.getNumberOfSpots())
            );
        }

        // Crear la reserva
        Reservation reservation = new Reservation(
                requestDTO.getPackageId(),
                requestDTO.getUserId(),
                requestDTO.getNumberOfSpots(),
                Reservation.ReservationStatus.PENDING
        );

        // Generar código de confirmación
        reservation.setConfirmationCode(ConfirmationCodeGenerator.generate());

        // Calcular precio total
        Double totalPrice = packageInfo.getPrice() * requestDTO.getNumberOfSpots();
        reservation.setTotalPrice(totalPrice);

        reservation.setNotes(requestDTO.getNotes());

        // Guardar la reserva
        Reservation savedReservation = reservationRepository.save(reservation);
        logger.info("Reserva creada exitosamente: ID={}, Código={}", savedReservation.getId(), savedReservation.getConfirmationCode());

        // Registrar en auditoría
        auditService.logAction(savedReservation.getId(), com.vhl.reservationservice.model.ReservationAudit.AuditAction.CREATED, "SYSTEM");

        return new ReservationResponseDTO(savedReservation);
    }

    @Override
    public ReservationResponseDTO getReservation(Long id) {
        logger.debug("Obteniendo reserva: {}", id);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Reserva no encontrada: {}", id);
                    return new PackageNotFoundException("Reserva no encontrada con ID: " + id);
                });
        
        return new ReservationResponseDTO(reservation);
    }

    @Override
    public List<ReservationResponseDTO> getReservationsByUserId(Long userId) {
        logger.debug("Obteniendo reservas del usuario: {}", userId);
        
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        return reservations.stream()
                .map(ReservationResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponseDTO> getReservationsByPackageId(Long packageId) {
        logger.debug("Obteniendo reservas del paquete: {}", packageId);
        
        List<Reservation> reservations = reservationRepository.findByPackageId(packageId);
        return reservations.stream()
                .map(ReservationResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO requestDTO) {
        logger.info("Actualizando reserva: {}", id);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException("Reserva no encontrada con ID: " + id));

        // Validar que solo se pueden actualizar reservas pendientes
        if (!reservation.getStatus().equals(Reservation.ReservationStatus.PENDING)) {
            throw new ReservationException("No se puede actualizar una reserva que ya ha sido confirmada o cancelada");
        }

        // Validar nuevos cupos
        if (!reservation.getPackageId().equals(requestDTO.getPackageId()) || 
            !reservation.getNumberOfSpots().equals(requestDTO.getNumberOfSpots())) {
            
            PackageServiceClient.PackageInfo packageInfo = packageServiceClient.getPackageInfo(requestDTO.getPackageId());
            if (packageInfo == null) {
                throw new PackageNotFoundException("Paquete no encontrado");
            }
            
            if (packageInfo.getAvailableSpots() < requestDTO.getNumberOfSpots()) {
                throw new InsufficientSpotsException("Cupos insuficientes para la actualización");
            }

            reservation.setTotalPrice(packageInfo.getPrice() * requestDTO.getNumberOfSpots());
        }

        reservation.setPackageId(requestDTO.getPackageId());
        reservation.setNumberOfSpots(requestDTO.getNumberOfSpots());
        reservation.setNotes(requestDTO.getNotes());
        reservation.setUpdatedAt(LocalDateTime.now());

        Reservation updatedReservation = reservationRepository.save(reservation);
        logger.info("Reserva actualizada: {}", id);

        auditService.logAction(id, com.vhl.reservationservice.model.ReservationAudit.AuditAction.UPDATED, "SYSTEM");

        return new ReservationResponseDTO(updatedReservation);
    }

    @Override
    public void cancelReservation(Long id) {
        logger.info("Cancelando reserva: {}", id);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException("Reserva no encontrada con ID: " + id));

        if (reservation.getStatus().equals(Reservation.ReservationStatus.CANCELLED)) {
            throw new ReservationException("La reserva ya ha sido cancelada");
        }

        Reservation.ReservationStatus oldStatus = reservation.getStatus();
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        
        logger.info("Reserva cancelada: {}", id);
        
        auditService.logAction(id, com.vhl.reservationservice.model.ReservationAudit.AuditAction.CANCELLED, 
                               oldStatus, Reservation.ReservationStatus.CANCELLED, "SYSTEM");
    }

    @Override
    public void confirmReservation(Long id) {
        logger.info("Confirmando reserva: {}", id);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException("Reserva no encontrada con ID: " + id));

        if (!reservation.getStatus().equals(Reservation.ReservationStatus.PENDING)) {
            throw new ReservationException("Solo se pueden confirmar reservas en estado pendiente");
        }

        Reservation.ReservationStatus oldStatus = reservation.getStatus();
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        
        logger.info("Reserva confirmada: {}", id);
        
        auditService.logAction(id, com.vhl.reservationservice.model.ReservationAudit.AuditAction.CONFIRMED,
                               oldStatus, Reservation.ReservationStatus.CONFIRMED, "SYSTEM");
    }

    @Override
    public List<ReservationResponseDTO> getAllReservations() {
        logger.debug("Obteniendo todas las reservas");
        
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(ReservationResponseDTO::new)
                .collect(Collectors.toList());
    }

    private void validateReservationRequest(ReservationRequestDTO requestDTO) {
        if (requestDTO.getUserId() == null || requestDTO.getUserId() <= 0) {
            throw new ReservationException("ID de usuario inválido");
        }

        if (requestDTO.getPackageId() == null || requestDTO.getPackageId() <= 0) {
            throw new ReservationException("ID de paquete inválido");
        }

        if (requestDTO.getNumberOfSpots() == null || requestDTO.getNumberOfSpots() <= 0) {
            throw new ReservationException("Número de cupos debe ser mayor a 0");
        }

        if (requestDTO.getNumberOfSpots() > maxSpotsPerReservation) {
            throw new ReservationException("El número máximo de cupos por reserva es: " + maxSpotsPerReservation);
        }
    }
}
