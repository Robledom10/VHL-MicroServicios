package com.hernandolopera.operation_servicio.excepcion;

import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.RespuestaErrorApi;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {
    @ExceptionHandler(RecursoNoEncontradoExcepcion.class)
    public ResponseEntity<RespuestaErrorApi> manejarNoEncontrado(RecursoNoEncontradoExcepcion excepcion, HttpServletRequest solicitud) {
        return construir(HttpStatus.NOT_FOUND, excepcion.getMessage(), solicitud, Map.of());
    }

    @ExceptionHandler(ExcepcionReglaNegocio.class)
    public ResponseEntity<RespuestaErrorApi> manejarReglaNegocio(ExcepcionReglaNegocio excepcion, HttpServletRequest solicitud) {
        return construir(HttpStatus.CONFLICT, excepcion.getMessage(), solicitud, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaErrorApi> manejarValidacion(MethodArgumentNotValidException excepcion, HttpServletRequest solicitud) {
        Map<String, String> campos = new LinkedHashMap<>();
        excepcion.getBindingResult().getFieldErrors().forEach(error -> campos.put(error.getField(), error.getDefaultMessage()));
        return construir(HttpStatus.BAD_REQUEST, "La solicitud tiene campos invalidos", solicitud, campos);
    }

    private ResponseEntity<RespuestaErrorApi> construir(HttpStatus codigo, String mensaje, HttpServletRequest solicitud, Map<String, String> campos) {
        return ResponseEntity.status(codigo).body(new RespuestaErrorApi(LocalDateTime.now(), codigo.value(),
            codigo.getReasonPhrase(), mensaje, solicitud.getRequestURI(), campos));
    }
}
