package com.fragua.LLMGateway.structure.shared.web;

import com.fragua.LLMGateway.structure.shared.exception.ConflictException;
import com.fragua.LLMGateway.structure.shared.exception.InvalidRequestException;
import com.fragua.LLMGateway.structure.shared.exception.RateLimitExceededException;
import com.fragua.LLMGateway.structure.shared.exception.ResourceNotFoundException;
import com.fragua.LLMGateway.structure.shared.exception.UnauthorizedException;
import com.fragua.LLMGateway.structure.shared.exception.UpstreamServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Traduce las excepciones de dominio a respuestas HTTP consistentes.
 * Las rutas /v1/** devuelven el formato de error de OpenAI para que
 * clientes como OpenCode puedan interpretarlo.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), "not_found_error", request);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequest(InvalidRequestException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "invalid_request_error", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");
        return build(HttpStatus.BAD_REQUEST, message, "invalid_request_error", request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), "authentication_error", request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflict(ConflictException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), "conflict_error", request);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Object> handleRateLimit(RateLimitExceededException ex, HttpServletRequest request) {
        return build(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), "rate_limit_error", request);
    }

    @ExceptionHandler(UpstreamServiceException.class)
    public ResponseEntity<Object> handleUpstream(UpstreamServiceException ex, HttpServletRequest request) {
        log.error("Error del servicio upstream (Ollama): {}", ex.getMessage(), ex);
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), "upstream_error", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "api_error", request);
    }

    private ResponseEntity<Object> build(HttpStatus status, String message, String type, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/v1/")) {
            return ResponseEntity.status(status).body(Map.of(
                    "error", Map.of(
                            "message", message,
                            "type", type,
                            "code", status.value()
                    )
            ));
        }

        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "path", request.getRequestURI()
        ));
    }
}
