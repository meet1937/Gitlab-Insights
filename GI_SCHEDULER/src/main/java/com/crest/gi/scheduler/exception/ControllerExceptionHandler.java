package com.crest.gi.scheduler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handle exception if any exception is thrown from the controller
 */
@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> someException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
}
