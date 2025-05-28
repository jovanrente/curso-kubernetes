package com.jovan.com.msvc_cursos.service;

import com.jovan.com.msvc_cursos.dto.CursoDto;
import com.jovan.com.msvc_cursos.dto.Usuario;

import java.util.List;
import java.util.Optional;

public interface CursoService {
    List<CursoDto> listar();
    CursoDto porId(Long id);
    CursoDto guardar(CursoDto curso);
    void eliminar(Long id);

    Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> eliminarUsuarioDelCurso(Long cursoId, Usuario usuario);

    Optional<CursoDto> porIdConUsuarios(Long id);

    void eliminarUsuarioDelCursobyIdUsuario(Long usuarioId);
} 