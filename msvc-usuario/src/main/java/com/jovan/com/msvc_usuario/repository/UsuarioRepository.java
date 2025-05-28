package com.jovan.com.msvc_usuario.repository;

import com.jovan.com.msvc_usuario.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    
    @Query("SELECT u FROM UsuarioEntity u WHERE u.id IN :ids")
    List<UsuarioEntity> findAllByIds(@Param("ids") List<Long> ids);
} 