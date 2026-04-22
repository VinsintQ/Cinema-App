package com.Cinema.App.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", "Access Denied");
        error.put("message", "Only ADMIN users can create movies");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
