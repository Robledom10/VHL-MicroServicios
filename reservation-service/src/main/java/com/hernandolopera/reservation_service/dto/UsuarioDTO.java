package com.hernandolopera.reservation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

	private Long id;
	private String nombre;
	private String apellido;
	private String email;
	private String telefono;
	private String rol;
	private Boolean activo;
}
