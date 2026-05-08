package com.hernandolopera.operation_servicio.excepcion;

public class RecursoNoEncontradoExcepcion extends RuntimeException {

    public RecursoNoEncontradoExcepcion(String message) {
        super(message);
    }
}
