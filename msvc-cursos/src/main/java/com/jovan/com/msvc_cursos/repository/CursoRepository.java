package com.jovan.com.msvc_cursos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jovan.com.msvc_cursos.entities.CursoEntity;

public interface CursoRepository extends JpaRepository<CursoEntity, Long> {

    Optional<CursoEntity> findByCursoUsuariosUsuarioId(Long usuarioId);
}
