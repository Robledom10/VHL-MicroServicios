package com.hernandolopera.reservation_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactoEmergenciaDTO {

	private Long id;

	@NotBlank
	private String nombre;

	@NotBlank
	private String parentesco;

	@NotBlank
	private String telefono;

	private String correo;
}
