package com.hernandolopera.reservation_service.entidades;

import java.io.Serializable;

public enum EstadoReserva implements Serializable {
	BLOQUEADA("Bloqueada"),
	PENDIENTE("Pendiente"),
	CONFIRMADA("Confirmada"),
	CANCELADA("Cancelada"),
	COMPLETADA("Completada");

	private final String descripcion;

	EstadoReserva(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
}
