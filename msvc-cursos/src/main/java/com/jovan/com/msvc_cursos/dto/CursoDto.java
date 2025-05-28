package com.jovan.com.msvc_cursos.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoDto {
    private Long id;
    @NotBlank(message = "El nombre del curso es requerido")
    private String nombre;
    @Builder.Default
    private List<Usuario> usuarios = new ArrayList<>();
    @Builder.Default
    private List<CursoUsuarioDto> cursoUsuarios = new ArrayList<>();

}
