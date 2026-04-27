package com.hernandolopera.auth_service.exception;

import java.nio.file.AccessDeniedException;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.hernandolopera.auth_service.dto.exception.ApiError;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                Map<String, String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .collect(Collectors.toMap(
                                                e -> e.getField(),
                                                e -> e.getDefaultMessage(),
                                                (msg1, msg2) -> msg1));

                ApiError apiError = ApiError.builder()
                                .timeStamp(LocalDateTime.now())
                                .status(HttpStatus.FORBIDDEN.value())
                                .error("BAD_REQUEST")
                                .message("Error de validaciones")
                                .path(request.getRequestURI())
                                .errors(errors)
                                .build();

                return ResponseEntity.badRequest().body(apiError);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
                ApiError apiError = ApiError.builder()
                                .timeStamp(LocalDateTime.now())
                                .status(HttpStatus.FORBIDDEN.value())
                                .error("Forbidden")
                                .message("No tienes permisos para acceder a este recurso")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(apiError);
        }

        @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
        public ResponseEntity<ApiError> handleBadCredentials(
                        Exception ex,
                        HttpServletRequest request) {

                ApiError apiError = ApiError.builder()
                                .timeStamp(LocalDateTime.now())
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Unauthorized")
                                .message("Credenciales inválidas")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(apiError);
        }

        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ApiError> handleCustom(ResponseStatusException ex, HttpServletRequest request) {
                ApiError apiError = ApiError.builder()
                                .timeStamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("BAD_REQUEST")
                                .message(ex.getReason())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(ex.getStatusCode())
                                .body(apiError);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiError> handleInvalidFormat(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {

                ApiError apiError = ApiError.builder()
                                .timeStamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Bad Request")
                                .message("Formato de datos inválido")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.badRequest().body(apiError);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiError> handleDuplicate(
                        DataIntegrityViolationException ex,
                        HttpServletRequest request) {

                ApiError apiError = ApiError.builder()
                                .timeStamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message("El correo ya está registrado")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
        }
}
