package com.jovan.com.msvc_usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {

    private Long id;
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email no es valido")
    private String email;
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 4, max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres")
    private String password;
}
