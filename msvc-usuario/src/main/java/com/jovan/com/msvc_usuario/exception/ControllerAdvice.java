package com.jovan.com.msvc_usuario.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jovan.com.msvc_usuario.dto.ErrorDto;
import com.jovan.com.msvc_usuario.dto.ErrorValidacion;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(RequestException.class)
    public ResponseEntity<ErrorDto> handleRequestException(RequestException e) {
        ErrorDto error = ErrorDto.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, e.getHttpStatus());
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorDto> handleDatabaseException(DatabaseException e) {
        ErrorDto error = ErrorDto.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, e.getHttpStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ErrorDto error = ErrorDto.builder()
                .code("DB-001")
                .message("Error de integridad en la base de datos: " + e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorDto error = ErrorDto.builder()
                .code("DB-002")
                .message("Entidad no encontrada: " + e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorDto> handlePersistenceException(PersistenceException e) {
        ErrorDto error = ErrorDto.builder()
                .code("DB-003")
                .message("Error de persistencia: " + e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<ErrorDto> handleJpaSystemException(JpaSystemException e) {
        ErrorDto error = ErrorDto.builder()
                .code("DB-004")
                .message("Error del sistema JPA: " + e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorValidacion> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorValidacion error = new ErrorValidacion();
        error.setFecha(LocalDateTime.now());
        error.setMensaje("Error de validación");
        
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            errores.put(err.getField(), err.getDefaultMessage());
        });
        error.setErrores(errores);
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorValidacion> handleConstraintViolationException(ConstraintViolationException ex) {
        ErrorValidacion error = new ErrorValidacion();
        error.setFecha(LocalDateTime.now());
        error.setMensaje("Error de validación de restricciones");
        
        Map<String, String> errores = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            errores.put(violation.getPropertyPath().toString(), violation.getMessage());
        });
        error.setErrores(errores);
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
