package com.jovan.com.msvc_usuario.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jovan.com.msvc_usuario.dto.UsuarioDto;
import com.jovan.com.msvc_usuario.service.KafkaProducer;
import com.jovan.com.msvc_usuario.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
public class Usuariocontroller {

    private final UsuarioService usuarioService;

    public Usuariocontroller(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioDto> findAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public UsuarioDto findById(@PathVariable Long id) {
        return usuarioService.findById(id);
    }

    @PostMapping
    public UsuarioDto save(@RequestBody @Valid UsuarioDto usuarioDto) {
        return usuarioService.save(usuarioDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
       
    }

    @PutMapping("/{id}")
    public UsuarioDto update(@PathVariable Long id, @RequestBody @Valid UsuarioDto usuarioDto) {
        UsuarioDto usuarioOld = usuarioService.findById(id);
        usuarioOld.setNombre(usuarioDto.getNombre());   
        usuarioOld.setEmail(usuarioDto.getEmail());
        usuarioOld.setPassword(usuarioDto.getPassword());
        return usuarioService.save(usuarioOld); 
    }

    @GetMapping("/usuarios-por-curso")
    public List<UsuarioDto> usuariosCurso(@RequestParam List<Long> ids) {
        return usuarioService.findAllByIds(ids);
    }


    

    
    
    
    
}
