package com.vhl.reservationservice.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vhl.reservationservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PackageNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handlePackageNotFoundException(
            PackageNotFoundException ex, WebRequest request) {
        logger.error("PackageNotFoundException: {}", ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false),
                "PACKAGE_NOT_FOUND"
        );
        
        ApiResponse<ErrorDetails> response = new ApiResponse<>(false, ex.getMessage(), errorDetails, "PACKAGE_NOT_FOUND");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleReservationNotFoundException(
            ReservationNotFoundException ex, WebRequest request) {
        logger.error("ReservationNotFoundException: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false),
                "RESERVATION_NOT_FOUND"
        );

        ApiResponse<ErrorDetails> response = new ApiResponse<>(false, ex.getMessage(), errorDetails, "RESERVATION_NOT_FOUND");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientSpotsException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleInsufficientSpotsException(
            InsufficientSpotsException ex, WebRequest request) {
        logger.error("InsufficientSpotsException: {}", ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false),
                "INSUFFICIENT_SPOTS"
        );
        
        ApiResponse<ErrorDetails> response = new ApiResponse<>(false, ex.getMessage(), errorDetails, "INSUFFICIENT_SPOTS");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleReservationException(
            ReservationException ex, WebRequest request) {
        logger.error("ReservationException: {}", ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false),
                ex.getErrorCode()
        );
        
        ApiResponse<ErrorDetails> response = new ApiResponse<>(false, ex.getMessage(), errorDetails, ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        logger.error("MethodArgumentNotValidException");
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(toSpanishFieldName(fieldName), errorMessage);
        });
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("fechaHora", LocalDateTime.now());
        errorResponse.put("estadoHttp", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("errores", errors);
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(false, "Validación fallida", errorResponse, "VALIDATION_ERROR");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        logger.error("MethodArgumentTypeMismatchException: {}", ex.getMessage());

        String message = "El parametro '" + ex.getName() + "' debe tener un valor numerico valido";
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message,
                request.getDescription(false),
                "PARAMETRO_INVALIDO"
        );

        ApiResponse<ErrorDetails> response = new ApiResponse<>(false, message, errorDetails, "PARAMETRO_INVALIDO");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private String toSpanishFieldName(String fieldName) {
        return switch (fieldName) {
            case "packageId" -> "idPaquete";
            case "userId" -> "idUsuario";
            case "numberOfSpots" -> "numeroCupos";
            case "notes" -> "notas";
            default -> fieldName;
        };
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleGlobalException(
            Exception ex, WebRequest request) {
        logger.error("Excepción no controlada", ex);
        
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                request.getDescription(false),
                "INTERNAL_ERROR"
        );
        
        ApiResponse<ErrorDetails> response = new ApiResponse<>(false, "Error interno del servidor", errorDetails, "INTERNAL_ERROR");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static class ErrorDetails {
        @JsonProperty("fechaHora")
        private LocalDateTime timestamp;
        @JsonProperty("estadoHttp")
        private int status;
        @JsonProperty("mensaje")
        private String message;
        @JsonProperty("ruta")
        private String path;
        @JsonProperty("codigo")
        private String code;

        public ErrorDetails(LocalDateTime timestamp, int status, String message, String path, String code) {
            this.timestamp = timestamp;
            this.status = status;
            this.message = message;
            this.path = path;
            this.code = code;
        }

        // Getters
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getPath() {
            return path;
        }

        public String getCode() {
            return code;
        }
    }
}
