package com.jovan.com.msvc_usuario.service;

import java.util.List;

import com.jovan.com.msvc_usuario.dto.UsuarioDto;

public interface UsuarioService {

    List<UsuarioDto> findAll();

    UsuarioDto findById(Long id);

    UsuarioDto save(UsuarioDto usuarioDto);

    void delete(Long id);

    List<UsuarioDto> findAllByIds(List<Long> ids);

}
