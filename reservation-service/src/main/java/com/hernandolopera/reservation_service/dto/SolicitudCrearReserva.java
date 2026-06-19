package com.hernandolopera.reservation_service.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudCrearReserva {

	@NotNull
	private Long idUsuario;

	private Long idPaquete;

	@NotNull
	@Min(1)
	private Integer personas;

	private List<AcompananteDTO> acompanantes;

	private List<@Valid ContactoEmergenciaDTO> contactosEmergencia;

	@NotBlank
	private String paqueteNombre;

	@NotBlank
	private String destino;

	@NotBlank
	private String fechaSalida;

	@NotBlank
	private String fechaRegreso;

	@NotBlank
	private String tipoHabitacion;

	private String solicitudEspecial;

	private String notas;

	@NotNull
	private BigDecimal total;

	private Long idViaje;
}
