package com.hernandolopera.reservation_service.dto;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudCrearReserva {

	@NotBlank
	private String clienteNombre;

	@NotBlank
	private String tipoDocumento;

	@NotBlank
	private String documento;

	@NotBlank
	@Email
	private String clienteEmail;

	@NotBlank
	private String clienteTelefono;

	@NotBlank
	private String ciudadResidencia;

	@NotNull
	@Min(1)
	private Integer personas;

	private List<AcompananteDTO> acompanantes;

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

	@NotBlank
	private String metodoPago;

	@NotBlank
	private String estadoPago;

	@NotNull
	private BigDecimal total;

	private Long idViaje;
}
