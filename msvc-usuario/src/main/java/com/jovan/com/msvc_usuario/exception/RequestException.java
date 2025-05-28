package com.jovan.com.msvc_usuario.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
public class RequestException extends RuntimeException {

    private String code;
    private HttpStatus httpStatus;

    public RequestException(String code, HttpStatus httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
