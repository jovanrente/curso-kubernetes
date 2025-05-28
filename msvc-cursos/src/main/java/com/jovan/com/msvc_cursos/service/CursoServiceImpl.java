package com.jovan.com.msvc_cursos.service;

import com.jovan.com.msvc_cursos.clients.UsuarioClientRest;
import com.jovan.com.msvc_cursos.constants.ErrorMessages;
import com.jovan.com.msvc_cursos.dto.CursoDto;
import com.jovan.com.msvc_cursos.dto.Usuario;
import com.jovan.com.msvc_cursos.entities.CursoEntity;
import com.jovan.com.msvc_cursos.entities.CursoUsuarioEntity;
import com.jovan.com.msvc_cursos.exception.RequestException;
import com.jovan.com.msvc_cursos.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jovan.com.msvc_cursos.mapper.CursoMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements CursoService {

    @Autowired
    private CursoRepository repository;

    @Autowired
    private UsuarioClientRest client;

    @Autowired
    private CursoMapper cursoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CursoDto> listar() {
        return repository.findAll().stream()
                .map(cursoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CursoDto porId(Long id) {
        return  cursoMapper.toDto(repository.findById(id)
        .orElseThrow(() -> new RequestException(ErrorMessages.CURSO_NOT_FOUND, HttpStatus.NOT_FOUND,
         String.format(ErrorMessages.CURSO_NOT_FOUND_MESSAGE, id))));
    }

    @Override
    @Transactional
    public CursoDto guardar(CursoDto cursoDto) {
        CursoEntity curso = cursoMapper.toEntity(cursoDto);
        return cursoMapper.toDto(repository.save(curso));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

   

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        Optional<CursoEntity> curso = repository.findById(cursoId);
        if (curso.isPresent()) {
            Usuario usuarioMsvc = client.detalle(usuario.getId());
            CursoUsuarioEntity cursoUsuario = CursoUsuarioEntity.builder()
                    .usuarioId(usuarioMsvc.getId())
                    .build();
            curso.get().addCursoUsuario(cursoUsuario);
            repository.save(curso.get());
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<CursoEntity> curso = repository.findById(cursoId);
        if (curso.isPresent()) {
            Usuario usuarioMsvc = client.crear(usuario);
            CursoUsuarioEntity cursoUsuario = CursoUsuarioEntity.builder()
                    .usuarioId(usuarioMsvc.getId())
                    .build();
            curso.get().addCursoUsuario(cursoUsuario);
            repository.save(curso.get());
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuarioDelCurso(Long cursoId, Usuario usuario) {
        Optional<CursoEntity> curso = repository.findById(cursoId);
        if (curso.isPresent()) {
            Usuario usuarioMsvc = client.detalle(usuario.getId());
            CursoUsuarioEntity cursoUsuario = CursoUsuarioEntity.builder()
                    .usuarioId(usuarioMsvc.getId())
                    .build();
            curso.get().removeCursoUsuario(cursoUsuario);
            repository.save(curso.get());
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void eliminarUsuarioDelCursobyIdUsuario(Long usuarioId) {
        Optional<CursoEntity> curso = repository.findByCursoUsuariosUsuarioId(usuarioId);
        if (curso.isPresent()) {
            CursoUsuarioEntity cursoUsuario = CursoUsuarioEntity.builder()
                    .usuarioId(usuarioId)
                    .build();
            curso.get().removeCursoUsuario(cursoUsuario);
            repository.save(curso.get());
          
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CursoDto> porIdConUsuarios(Long id) {
        Optional<CursoEntity> curso = repository.findById(id);
        if (curso.isPresent()) {
            CursoDto cursoDto = cursoMapper.toDto(curso.get());
            List<Usuario> usuarios = client.obtenerAlumnosPorCurso(curso.get().getCursoUsuarios()
             .stream()
                .map(CursoUsuarioEntity::getUsuarioId)
                .collect(Collectors.toList()));
            cursoDto.setUsuarios(usuarios);
            return Optional.of(cursoDto);
        }
        return Optional.empty();
    }
} 