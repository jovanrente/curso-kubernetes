package com.jovan.com.msvc_cursos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jovan.com.msvc_cursos.constants.ErrorMessages;
import com.jovan.com.msvc_cursos.dto.CursoDto;
import com.jovan.com.msvc_cursos.dto.Usuario;
import com.jovan.com.msvc_cursos.exception.RequestException;
import com.jovan.com.msvc_cursos.service.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cursos")
@Tag(name = "Cursos", description = "API para la gestión de cursos")
public class CursoController {


    private CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @Operation(summary = "Listar todos los cursos", description = "Retorna una lista de todos los cursos disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa", 
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CursoDto.class)) })
    })
    @GetMapping
    public List<CursoDto> listar() {
        return cursoService.listar();
    }

    @Operation(summary = "Obtener un curso por ID", description = "Retorna un curso según el ID proporcionado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso encontrado", 
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CursoDto.class)) }),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public CursoDto obtenerCurso(
        @Parameter(description = "ID del curso a buscar", required = true) @PathVariable Long id) {
        return cursoService.porIdConUsuarios(id)
        .orElseThrow(() -> new RequestException(ErrorMessages.CURSO_NOT_FOUND, HttpStatus.NOT_FOUND,
         String.format(ErrorMessages.CURSO_NOT_FOUND_MESSAGE, id)));
    }

    @Operation(summary = "Crear un nuevo curso", description = "Crea un nuevo curso con los datos proporcionados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso creado correctamente", 
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CursoDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public CursoDto guardar(
        @Parameter(description = "Datos del curso a crear", required = true) @RequestBody @Valid CursoDto cursoDto) {
        return cursoService.guardar(cursoDto);
    }

    @Operation(summary = "Actualizar un curso existente", description = "Actualiza un curso según el ID proporcionado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso actualizado correctamente", 
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CursoDto.class)) }),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public CursoDto actualizar(
        @Parameter(description = "ID del curso a actualizar", required = true) @PathVariable Long id,
        @Parameter(description = "Nuevos datos del curso", required = true) @RequestBody @Valid CursoDto cursoDto) {
        CursoDto cursoDtoActualizado = cursoService.porId(id);
        cursoDtoActualizado.setNombre(cursoDto.getNombre());
        return cursoService.guardar(cursoDtoActualizado);
    }

    @Operation(summary = "Eliminar un curso", description = "Elimina un curso según el ID proporcionado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Curso eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public void eliminar(
        @Parameter(description = "ID del curso a eliminar", required = true) @PathVariable Long id) {
        cursoService.eliminar(id);
    }

    @Operation(summary = "Asignar un usuario a un curso")
    @PutMapping("/curso-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@PathVariable Long cursoId, @RequestBody @Valid Usuario usuario) {
        return cursoService.asignarUsuario(usuario, cursoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un usuario")
    @PostMapping("/usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@PathVariable Long cursoId, @RequestBody @Valid Usuario usuario) {
        return cursoService.crearUsuario(usuario, cursoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un usuario de un curso")
    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long cursoId, @Parameter(description = "Usuario a eliminar", required = true) @RequestBody Usuario usuario) {
        return cursoService.eliminarUsuarioDelCurso(cursoId, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
