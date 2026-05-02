package com.hernandolopera.reservation_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
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
public class ViajeroDTO {

	private Long id;

	@NotBlank(message = "El nombre del viajero es obligatorio")
	private String nombre;

	@NotBlank(message = "El apellido del viajero es obligatorio")
	private String apellido;

	@NotBlank(message = "El documento del viajero es obligatorio")
	private String documento;

	@NotBlank(message = "El tipo de documento es obligatorio")
	private String tipoDocumento;

	@NotNull(message = "La fecha de nacimiento es obligatoria")
	private LocalDate fechaNacimiento;

	@Email(message = "El email debe ser válido")
	@NotBlank(message = "El email es obligatorio")
	private String email;

	private String telefono;
	private String genero;
	private String nacionalidad;
	private Boolean datosCompletos;
	private Boolean documentosVerificados;
	private LocalDateTime fechaCreacion;
	private LocalDateTime fechaActualizacion;

}
