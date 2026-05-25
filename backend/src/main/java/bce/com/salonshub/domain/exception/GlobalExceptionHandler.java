package bce.com.salonshub.domain.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
@Order(-2)
@Slf4j
public class GlobalExceptionHandler {

    private Mono<ResponseEntity<Map<String, Object>>> build(
        ServerWebExchange exchange, HttpStatusCode status, String message) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("path", exchange.getRequest().getPath().value());
        body.put("status", status.value());
        body.put("error", status.toString());
        body.put("message", message);
        body.put("requestId", exchange.getRequest().getId());
        return Mono.just(ResponseEntity.status(status).body(body));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgumentException(
        IllegalArgumentException ex, ServerWebExchange exchange) {
        return build(exchange, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNoSuchElement(
        NoSuchElementException ex, ServerWebExchange exchange) {
        return build(exchange, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleServerWebInput(
        ServerWebInputException ex, ServerWebExchange exchange) {

        String msg = ex.getReason();
        Throwable cause = ex.getCause();

        if (cause instanceof DecodingException de) {
            if (de.getCause() instanceof InvalidFormatException ife) {
                var path = ife.getPath();
                String field = !path.isEmpty() ? path.getLast().getFieldName() : "unknown";
                msg = "Invalid value for field '" + field + "'.";
            } else if (de.getCause() instanceof JsonMappingException jme) {
                msg = jme.getMessage();
            }
        }

        return build(exchange, HttpStatus.BAD_REQUEST, msg != null ? msg : "Malformed request payload.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleResponseStatus(
        ResponseStatusException ex, ServerWebExchange exchange) {
        return build(exchange, ex.getStatusCode(), ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleAny(
        Exception ex, ServerWebExchange exchange) {
        log.error(ex.getMessage(), ex);
        return build(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + ex.getMessage());
    }
}