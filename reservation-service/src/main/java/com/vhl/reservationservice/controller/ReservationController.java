package com.vhl.reservationservice.controller;

import com.vhl.reservationservice.dto.ApiResponse;
import com.vhl.reservationservice.dto.ReservationRequestDTO;
import com.vhl.reservationservice.dto.ReservationResponseDTO;
import com.vhl.reservationservice.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "API para gestionar reservas de paquetes turísticos")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Crear nueva reserva", description = "Crea una nueva reserva para un paquete turístico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Paquete no encontrado")
    })
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> createReservation(
            @Valid @RequestBody ReservationRequestDTO requestDTO) {
        logger.info("POST /api/reservations - Creando nueva reserva");
        try {
            ReservationResponseDTO response = reservationService.createReservation(requestDTO);
            return new ResponseEntity<>(
                new ApiResponse<>(true, "Reserva creada exitosamente", response, "RES_CREATED"),
                HttpStatus.CREATED
            );
        } catch (Exception e) {
            logger.error("Error al crear reserva", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID", description = "Obtiene los detalles de una reserva específica")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> getReservation(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        logger.info("GET /api/reservations/{} - Obteniendo reserva", id);
        
        ReservationResponseDTO response = reservationService.getReservation(id);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reserva encontrada", response)
        );
    }

    @GetMapping
    @Operation(summary = "Obtener todas las reservas", description = "Lista todas las reservas en el sistema")
    public ResponseEntity<ApiResponse<List<ReservationResponseDTO>>> getAllReservations() {
        logger.info("GET /api/reservations - Obteniendo todas las reservas");
        
        List<ReservationResponseDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reservas obtenidas exitosamente", reservations)
        );
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener reservas por usuario", description = "Lista todas las reservas de un usuario específico")
    public ResponseEntity<ApiResponse<List<ReservationResponseDTO>>> getReservationsByUserId(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        logger.info("GET /api/reservations/user/{} - Obteniendo reservas del usuario", userId);
        
        List<ReservationResponseDTO> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reservas del usuario obtenidas", reservations)
        );
    }

    @GetMapping("/package/{packageId}")
    @Operation(summary = "Obtener reservas por paquete", description = "Lista todas las reservas de un paquete turístico específico")
    public ResponseEntity<ApiResponse<List<ReservationResponseDTO>>> getReservationsByPackageId(
            @Parameter(description = "ID del paquete") @PathVariable Long packageId) {
        logger.info("GET /api/reservations/package/{} - Obteniendo reservas del paquete", packageId);
        
        List<ReservationResponseDTO> reservations = reservationService.getReservationsByPackageId(packageId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reservas del paquete obtenidas", reservations)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reserva", description = "Actualiza los detalles de una reserva pendiente")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> updateReservation(
            @Parameter(description = "ID de la reserva") @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDTO requestDTO) {
        logger.info("PUT /api/reservations/{} - Actualizando reserva", id);
        
        ReservationResponseDTO response = reservationService.updateReservation(id, requestDTO);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reserva actualizada exitosamente", response)
        );
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirmar reserva", description = "Confirma una reserva que está en estado pendiente")
    public ResponseEntity<ApiResponse<String>> confirmReservation(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        logger.info("PUT /api/reservations/{}/confirm - Confirmando reserva", id);
        
        reservationService.confirmReservation(id);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reserva confirmada exitosamente", "OK", "RES_CONFIRMED")
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva existente")
    public ResponseEntity<ApiResponse<String>> cancelReservation(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        logger.info("DELETE /api/reservations/{} - Cancelando reserva", id);
        
        reservationService.cancelReservation(id);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reserva cancelada exitosamente", "OK", "RES_CANCELLED")
        );
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica el estado del servicio")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Servicio en línea", "OK")
        );
    }
}
