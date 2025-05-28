package com.jovan.com.msvc_usuario.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jovan.com.msvc_usuario.constants.ErrorMessages;
import com.jovan.com.msvc_usuario.dto.UsuarioDto;
import com.jovan.com.msvc_usuario.entities.UsuarioEntity;
import com.jovan.com.msvc_usuario.exception.RequestException;
import com.jovan.com.msvc_usuario.repository.UsuarioRepository;
import com.jovan.com.msvc_usuario.mapper.UsuarioMapper;
@Service
public class UsusuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;
    private final KafkaProducer kafkaProducer;


    public UsusuarioServiceImpl(UsuarioRepository usuarioRepository, KafkaProducer kafkaProducer) {
        this.usuarioRepository = usuarioRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDto> findAll() {
        return usuarioRepository.findAll().stream().map(UsuarioMapper::toUsuarioDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto findById(Long id) {
        return UsuarioMapper.toUsuarioDto(usuarioRepository.findById(id).orElseThrow(() -> 
        new RequestException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND,
         String.format(ErrorMessages.USER_NOT_FOUND_MESSAGE, id))));
    }

    @Override
    @Transactional
    public UsuarioDto save(UsuarioDto usuarioDto) {
        return UsuarioMapper.toUsuarioDto(usuarioRepository.save(UsuarioMapper.toUsuarioEntity(usuarioDto)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Optional<UsuarioEntity> usuario = usuarioRepository.findById(id);
        if(usuario.isPresent()){
            usuarioRepository.deleteById(id); 
            kafkaProducer.sendDeleteUser("myTopic", List.of(id));
        }else{
            throw new RequestException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND,
             String.format(ErrorMessages.USER_NOT_FOUND_MESSAGE, id));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDto> findAllByIds(List<Long> ids) {
        return usuarioRepository.findAllById(ids)
        .stream()
        .map(UsuarioMapper::toUsuarioDto)
        .collect(Collectors.toList());
    }

}
