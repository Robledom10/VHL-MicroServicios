package com.hernandolopera.reservation_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.hernandolopera.reservation_service.entidades.EstadoReserva;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaDTO {

	private Long id;
	private String numeroReserva;
	private Long idPaquete;
	private Long idUsuario;
	private Integer cantidadPasajeros;
	private BigDecimal precioTotal;
	private LocalDateTime fechaInicioViaje;
	private LocalDateTime fechaFinViaje;
	private EstadoReserva estado;
	private Boolean pagoVerificado;
	private LocalDateTime fechaCreacion;
	private LocalDateTime fechaActualizacion;
	private LocalDateTime fechaConfirmacion;
	private List<ViajeroDTO> viajeros;
	private String notas;

}
