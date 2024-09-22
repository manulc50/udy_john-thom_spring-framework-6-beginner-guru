package com.mlorenzo.spring6restmvc.controllers;


import com.mlorenzo.spring6restmvc.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionController {

    // Nota: Estos métodos pueden ser públicos o privados

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<Void> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    // Validaciones a nivel de controlador
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private List<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ex.getFieldErrors().stream()
                .map(fe -> Map.of(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    // Validaciones a nivel de persistencia(JPA)
    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<List<Map<String, String>>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<Map<String, String>> errorsMap = ex.getConstraintViolations().stream()
                .map(cv -> Map.of(cv.getPropertyPath().toString(), cv.getMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errorsMap);
    }
}
