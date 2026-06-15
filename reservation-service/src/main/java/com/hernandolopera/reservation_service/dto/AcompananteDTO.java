package com.hernandolopera.reservation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcompananteDTO {
	private Long id;
	private String nombre;
	private String fechaNacimiento;
	private String tipoDocumento;
	private String documento;
}
