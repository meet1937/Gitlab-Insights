package com.crest.gi.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class provide exception handling for controllers
 */
@RestControllerAdvice
public class ControllerExceptionHandler {
    /**
     * This class will handle all exceptions thrown by the controller
     * @param exception
     * @return
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> someException(Exception exception) {
        exception.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
}
