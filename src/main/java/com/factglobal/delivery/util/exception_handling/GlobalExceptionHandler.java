package com.factglobal.delivery.util.exception_handling;


import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Map<String, Object>> entityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        final Map<String, Object> body = new TreeMap<>();

        body.put("status", HttpStatus.NOT_FOUND);
        body.put("error", "EntityNotFoundException");
        body.put("message", ex.getMessage());
        body.put("details", "Entity not found");
        body.put("path", request.getServletPath());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> entitiesNotFoundException(NoSuchElementException ex, HttpServletRequest request) {
        final Map<String, Object> body = new TreeMap<>();

        body.put("status", HttpStatus.NOT_FOUND);
        body.put("error", "NoSuchElementException");
        body.put("message", ex.getMessage());
        body.put("details", "Entities not found");
        body.put("path", request.getServletPath());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> entityExistsException(IllegalArgumentException ex, HttpServletRequest request) {
        final Map<String, Object> body = new TreeMap<>();

        body.put("status", HttpStatus.BAD_REQUEST);
        body.put("error", "IllegalArgumentException");
        body.put("message", ex.getMessage());
        body.put("details", "Change the data in the request body");
        body.put("path", request.getServletPath());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> entityStateException(IllegalStateException ex, HttpServletRequest request) {
        final Map<String, Object> body = new TreeMap<>();

        body.put("status", HttpStatus.BAD_REQUEST);
        body.put("error", "IllegalStateException");
        body.put("message", ex.getMessage());
        body.put("details", "Invalid object state");
        body.put("path", request.getServletPath());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<Map<String, Object>> unknownException(Exception ex, HttpServletRequest request) {
//        final Map<String, Object> body = new TreeMap<>();
//
//        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
//        body.put("error", "UnknownException");
//        body.put("message", ex.getMessage());
//        body.put("details", "Error");
//        body.put("path", request.getServletPath());
//
//        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
