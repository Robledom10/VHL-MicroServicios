package com.hernandolopera.operation_service.excepciones;

public class ExcepcionReglaNegocio extends RuntimeException {
    public ExcepcionReglaNegocio(String mensaje) {
        super(mensaje);
    }
}
