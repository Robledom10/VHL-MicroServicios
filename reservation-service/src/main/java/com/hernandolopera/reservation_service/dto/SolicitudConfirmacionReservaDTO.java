package com.hernandolopera.reservation_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudConfirmacionReservaDTO {

	@NotNull(message = "El ID de la reserva es obligatorio")
	private Long idReserva;

	private String numeroReserva;

	@NotNull(message = "La cantidad de pasajeros debe ser especificada")
	@Positive(message = "La cantidad de pasajeros debe ser mayor a 0")
	private Integer cantidadPasajeros;

	@NotNull(message = "El monto del pago es obligatorio")
	@Positive(message = "El monto debe ser mayor a 0")
	private BigDecimal montoPago;

	@NotNull(message = "El estado del pago es obligatorio")
	private Boolean pagoVerificado;

	private String referenciaTransaccion;
	private LocalDateTime fechaPago;
	private String metodoPago;

}
