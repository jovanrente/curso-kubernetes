package com.jovan.com.msvc_cursos.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ErrorValidacion {
    private LocalDateTime fecha;
    private String mensaje;
    private Map<String, String> errores;

    public ErrorValidacion() {
        this.errores = new HashMap<>();
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Map<String, String> getErrores() {
        return errores;
    }

    public void setErrores(Map<String, String> errores) {
        this.errores = errores;
    }
} 