package com.hernandolopera.reservation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaRegistroViajeroDTO {

	private Boolean exito;
	private String mensaje;
	private Long idViajero;
	private Long idReserva;
	private String detalleError;

}
