package com.hernandolopera.reservation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaConfirmacionDTO {

	private Boolean exito;
	private String mensaje;
	private String numeroReserva;
	private String estadoReserva;
	private String detalleError;

}
