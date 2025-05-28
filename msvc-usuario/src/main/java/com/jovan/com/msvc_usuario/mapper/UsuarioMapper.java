package com.jovan.com.msvc_usuario.mapper;

import com.jovan.com.msvc_usuario.dto.UsuarioDto;
import com.jovan.com.msvc_usuario.entities.UsuarioEntity;

public class UsuarioMapper {

    public static UsuarioDto toUsuarioDto(UsuarioEntity usuarioEntity) {
        return UsuarioDto.builder()
                .id(usuarioEntity.getId())
                .nombre(usuarioEntity.getNombre())
                .email(usuarioEntity.getEmail())
                .password(usuarioEntity.getPassword())
                .build();
    }

    public static UsuarioEntity toUsuarioEntity(UsuarioDto usuarioDto) {
        return UsuarioEntity.builder()
                .id(usuarioDto.getId())
                .nombre(usuarioDto.getNombre())
                .email(usuarioDto.getEmail())
                .password(usuarioDto.getPassword())
                .build();
    }
}


