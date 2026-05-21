package com.hernandolopera.operation_servicio.excepcion;

public class ExcepcionReglaNegocio extends RuntimeException {
    public ExcepcionReglaNegocio(String mensaje) {
        super(mensaje);
    }
}
