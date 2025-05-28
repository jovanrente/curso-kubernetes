package com.jovan.com.msvc_cursos.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.jovan.com.msvc_cursos.dto.Usuario;

@FeignClient(name = "msvc-usuarios", url = "${msvc.usuarios.url}", fallback = UsuarioClientFallback.class)
public interface UsuarioClientRest {
    
    @GetMapping("usuario/{id}")
    Usuario detalle(@PathVariable Long id);

    @PostMapping("/usuario")
    Usuario crear(@RequestBody Usuario usuario);

    @GetMapping("usuario/usuarios-por-curso")
    List<Usuario> obtenerAlumnosPorCurso(@RequestParam Iterable<Long> ids);

}
