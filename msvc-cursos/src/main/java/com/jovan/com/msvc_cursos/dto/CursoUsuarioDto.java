package com.jovan.com.msvc_cursos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar la relación entre un curso y un usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para representar la relación entre un curso y un usuario")
public class CursoUsuarioDto {
    
    @Schema(description = "ID del registro de relación curso-usuario", example = "1")
    private Long id;
    
    @Schema(description = "ID del usuario", example = "1")
    @NotNull(message = "El ID del usuario es requerido")
    private Long usuarioId;
    
} 