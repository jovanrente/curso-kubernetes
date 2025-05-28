package com.jovan.com.msvc_usuario.constants;

public class ErrorMessages {
    public static final String USER_NOT_FOUND = "P-30";
    public static final String USER_NOT_FOUND_MESSAGE = "Usuario con id: %s, no encontrado";
    
    // Códigos de error de base de datos
    public static final String DB_INTEGRITY_VIOLATION = "DB-001";
    public static final String DB_ENTITY_NOT_FOUND = "DB-002";
    public static final String DB_PERSISTENCE_ERROR = "DB-003";
    public static final String DB_JPA_SYSTEM_ERROR = "DB-004";
    
    // Mensajes de error de base de datos
    public static final String DB_INTEGRITY_VIOLATION_MESSAGE = "Error de integridad en la base de datos: %s";
    public static final String DB_ENTITY_NOT_FOUND_MESSAGE = "Entidad no encontrada: %s";
    public static final String DB_PERSISTENCE_ERROR_MESSAGE = "Error de persistencia: %s";
    public static final String DB_JPA_SYSTEM_ERROR_MESSAGE = "Error del sistema JPA: %s";
}
