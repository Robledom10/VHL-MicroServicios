package com.hernandolopera.reservation_service.entidades;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EstadoReservaConverter implements AttributeConverter<EstadoReserva, String> {

	@Override
	public String convertToDatabaseColumn(EstadoReserva estado) {
		return estado == null ? null : estado.name().toLowerCase();
	}

	@Override
	public EstadoReserva convertToEntityAttribute(String estado) {
		return estado == null ? null : EstadoReserva.valueOf(estado.toUpperCase());
	}
}
