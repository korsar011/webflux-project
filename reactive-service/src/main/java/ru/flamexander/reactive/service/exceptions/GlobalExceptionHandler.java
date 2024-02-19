package ru.flamexander.reactive.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorDto> handleAppException(AppException e) {
        return new ResponseEntity<>(
                new ErrorDto(e.getCode()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler({RuntimeException.class, InterruptedException.class})
    public Mono<ResponseEntity<String>> handleSlowServiceException(Exception e) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service is temporarily unavailable."));
    }
}
