package com.jovan.com.msvc_cursos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jovan.com.msvc_cursos.dto.ErrorDto;
import com.jovan.com.msvc_cursos.dto.ErrorValidacion;

import feign.FeignException;
import feign.FeignException.FeignClientException;
import feign.FeignException.FeignServerException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorDto> handleFeignException(FeignException ex) {
        log.error("Error en la comunicación con el microservicio: {}", ex.getMessage());
        
        String errorMessage;
        String errorCode;
        HttpStatus status;

        if (ex instanceof FeignClientException) {
            errorMessage = "Error en la comunicación con el microservicio (Cliente)";
            errorCode = "FEIGN-CLIENT-" + ex.status();
            status = HttpStatus.valueOf(ex.status());
        } else if (ex instanceof FeignServerException) {
            errorMessage = "Error en la comunicación con el microservicio (Servidor)";
            errorCode = "FEIGN-SERVER-" + ex.status();
            status = HttpStatus.valueOf(ex.status());
        } else {
            errorMessage = "Error en la comunicación con el microservicio";
            errorCode = "FEIGN-ERROR";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ErrorDto error = ErrorDto.builder()
                .code(errorCode)
                .message(errorMessage)
                .build();

        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorDto> handleFeignNotFoundException(FeignException.NotFound ex) {
        log.error("Recurso no encontrado en el microservicio: {}", ex.getMessage());
        
        ErrorDto error = ErrorDto.builder()
                .code("FEIGN-404")
                .message("Recurso no encontrado en el microservicio")
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public ResponseEntity<ErrorDto> handleFeignServiceUnavailableException(FeignException.ServiceUnavailable ex) {
        log.error("Servicio no disponible: {}", ex.getMessage());
        
        ErrorDto error = ErrorDto.builder()
                .code("FEIGN-503")
                .message("El servicio no está disponible en este momento")
                .build();

        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ErrorDto> handleKafkaException(KafkaException ex) {
        log.error("Error en la comunicación con Kafka: {}", ex.getMessage());
        
        String errorMessage;
        String errorCode;
        
        if (ex.getCause() instanceof UnknownHostException) {
            errorMessage = "No se puede conectar con el servidor Kafka. Verifique que el servicio esté disponible.";
            errorCode = "KAFKA-001";
        } else {
            errorMessage = "Error en la comunicación con Kafka: " + ex.getMessage();
            errorCode = "KAFKA-000";
        }

        ErrorDto error = ErrorDto.builder()
                .code(errorCode)
                .message(errorMessage)
                .build();

        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
