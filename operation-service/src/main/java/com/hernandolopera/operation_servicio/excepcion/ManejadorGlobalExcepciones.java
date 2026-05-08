package com.hernandolopera.operation_servicio.excepcion;

import com.hernandolopera.operation_servicio.transferencia.RespuestaErrorApi;
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
    public ResponseEntity<RespuestaErrorApi> handleNotFound(RecursoNoEncontradoExcepcion excepcion, HttpServletRequest solicitud) {
        return build(HttpStatus.NOT_FOUND, excepcion.getMessage(), solicitud, Map.of());
    }

    @ExceptionHandler(ExcepcionReglaNegocio.class)
    public ResponseEntity<RespuestaErrorApi> handleBusiness(ExcepcionReglaNegocio excepcion, HttpServletRequest solicitud) {
        return build(HttpStatus.CONFLICT, excepcion.getMessage(), solicitud, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaErrorApi> handleValidation(MethodArgumentNotValidException excepcion, HttpServletRequest solicitud) {
        Map<String, String> fields = new LinkedHashMap<>();
        excepcion.getBindingResult().getFieldErrors()
            .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "La solicitud tiene campos invalidos", solicitud, fields);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaErrorApi> handleUnexpected(Exception excepcion, HttpServletRequest solicitud) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servicio de operaciones", solicitud, Map.of());
    }

    private ResponseEntity<RespuestaErrorApi> build(
        HttpStatus estado,
        String message,
        HttpServletRequest solicitud,
        Map<String, String> fields
    ) {
        RespuestaErrorApi respuesta = new RespuestaErrorApi(
            LocalDateTime.now(),
            estado.value(),
            estado.getReasonPhrase(),
            message,
            solicitud.getRequestURI(),
            fields
        );
        return ResponseEntity.status(estado).body(respuesta);
    }
}
