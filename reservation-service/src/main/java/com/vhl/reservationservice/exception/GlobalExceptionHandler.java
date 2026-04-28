package com.vhl.reservationservice.exception;

import com.vhl.reservationservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
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
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("errors", errors);
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(false, "Validación fallida", errorResponse, "VALIDATION_ERROR");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
        private LocalDateTime timestamp;
        private int status;
        private String message;
        private String path;
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
