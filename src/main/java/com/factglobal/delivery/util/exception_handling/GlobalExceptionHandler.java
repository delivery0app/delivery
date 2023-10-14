package com.factglobal.delivery.util.exception_handling;

import jakarta.persistence.EntityExistsException;
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

        body.put("status", HttpStatus.BAD_REQUEST);
        body.put("error", "EntityNotFoundException");
        body.put("message", ex.getMessage());
        body.put("details", "Entity not found");
        body.put("path", request.getServletPath());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> entitiesNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        final Map<String, Object> body = new TreeMap<>();

        body.put("status", HttpStatus.BAD_REQUEST);
        body.put("error", "NoSuchElementException");
        body.put("message", ex.getMessage());
        body.put("details", "No orders found");
        body.put("path", request.getServletPath());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
//    @ExceptionHandler({EntityExistsException.class})
//    public ResponseEntity<Map<String, Object>> entityExistsException(EntityExistsException ex, HttpServletRequest request) {
//        final Map<String, Object> body = new TreeMap<>();
//
//        body.put("status", HttpStatus.BAD_REQUEST);
//        body.put("error", "EntityExistsException");
//        body.put("message", ex.getMessage());
//        body.put("details", "Change the data in the request body");
//        body.put("path", request.getServletPath());
//
//        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
//    }
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map<String, Object>> unknownException(Exception ex, HttpServletRequest request) {
        final Map<String, Object> body = new TreeMap<>();

        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        body.put("error", "UnknownException");
        body.put("message", ex.getMessage());
        body.put("details", "Error");
        body.put("path", request.getServletPath());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
