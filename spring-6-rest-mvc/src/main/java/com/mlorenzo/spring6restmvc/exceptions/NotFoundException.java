package com.mlorenzo.spring6restmvc.exceptions;

// Se comenta porque ahora usamos un manejador global de excepciones para todos los controladores
//@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource Not Found")
public class NotFoundException extends RuntimeException {
}
