package com.grababite.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // This annotation makes this class handle exceptions across the entire application
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) // This method handles ResourceNotFoundException
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage()); // The message from your exception

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // You can add more @ExceptionHandler methods here for other types of exceptions.
    // For example, to handle validation errors (MethodArgumentNotValidException),
    // or general unexpected errors (Exception.class).
}
