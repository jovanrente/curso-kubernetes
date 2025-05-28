package com.jovan.com.msvc_usuario.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {
    private String message;
    private String code;
}
