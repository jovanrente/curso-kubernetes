package com.jovan.com.msvc_cursos.mapper;

import com.jovan.com.msvc_cursos.dto.CursoDto;
import com.jovan.com.msvc_cursos.dto.CursoUsuarioDto;
import com.jovan.com.msvc_cursos.entities.CursoEntity;
import com.jovan.com.msvc_cursos.entities.CursoUsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CursoMapper {
  
    CursoMapper INSTANCE = Mappers.getMapper(CursoMapper.class);

    CursoDto toDto(CursoEntity curso);

    CursoEntity toEntity(CursoDto cursoDto);

    CursoUsuarioDto toDto(CursoUsuarioEntity cursoUsuario);
} 