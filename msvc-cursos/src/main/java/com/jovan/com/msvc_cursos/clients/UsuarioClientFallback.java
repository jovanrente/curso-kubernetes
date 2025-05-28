package com.jovan.com.msvc_cursos.clients;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.jovan.com.msvc_cursos.dto.Usuario;

@Component
public class UsuarioClientFallback implements UsuarioClientRest {

    @Override
    public Usuario detalle(Long id) {
        return Usuario.builder()
                .id(id)
                .nombre("Usuario no disponible")
                .email("no-disponible@email.com")
                .password("")
                .build();
    }

    @Override
    public Usuario crear(Usuario usuario) {
        return Usuario.builder()
                .id(0L)
                .nombre("Error al crear usuario")
                .email("error@email.com")
                .password("")
                .build();
    }

    @Override
    public List<Usuario> obtenerAlumnosPorCurso(Iterable<Long> ids) {
        return new ArrayList<>();
    }
} 