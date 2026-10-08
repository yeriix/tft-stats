package com.yeriix.tftstats.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestClientResponseException.class)
    public ProblemDetail handleRiotError(RestClientResponseException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String detail = switch (status.value()) {
            case 404 -> "Jugador no encontrado";
            case 403 -> "API key de Riot inválida o caducada";
            case 429 -> "Demasiadas peticiones a Riot, inténtalo en unos segundos";
            default -> "Error al comunicarse con la API de Riot";
        };
        return ProblemDetail.forStatusAndDetail(status, detail);
    }
}