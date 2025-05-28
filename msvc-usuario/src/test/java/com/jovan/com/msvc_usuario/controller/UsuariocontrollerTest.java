package com.jovan.com.msvc_usuario.controller; // Singular

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovan.com.msvc_usuario.controllers.Usuariocontroller; // Import from plural
import com.jovan.com.msvc_usuario.dto.UsuarioDto;
import com.jovan.com.msvc_usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Usuariocontroller.class) // Refers to imported Usuariocontroller
public class UsuariocontrollerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    private UsuarioDto usuarioDto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        usuarioDto = UsuarioDto.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testFindAll() throws Exception {
        when(usuarioService.findAll()).thenReturn(Collections.singletonList(usuarioDto));

        mockMvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is(usuarioDto.getNombre())));
    }

    @Test
    void testFindById_Success() throws Exception {
        when(usuarioService.findById(anyLong())).thenReturn(usuarioDto);

        mockMvc.perform(get("/usuario/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre", is(usuarioDto.getNombre())));
    }

    // Add a test for findById when user is not found (later, need RequestException)

    @Test
    void testSave() throws Exception {
        when(usuarioService.save(any(UsuarioDto.class))).thenReturn(usuarioDto);

        mockMvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isOk()) // Should be 201 Created for save, but controller returns the object directly with 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre", is(usuarioDto.getNombre())));
    }
    
    @Test
    void testSave_InvalidData() throws Exception {
        UsuarioDto invalidDto = UsuarioDto.builder().id(2L).nombre("").email("invalid").password("123").build();

        mockMvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); // Assuming ControllerAdvice handles validation
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(usuarioService).delete(anyLong());

        mockMvc.perform(delete("/usuario/{id}", 1L))
                .andExpect(status().isOk()); // Controller returns void, so 200 OK is fine
    }

    @Test
    void testUpdate() throws Exception {
        UsuarioDto updatedDto = UsuarioDto.builder()
                .id(1L)
                .nombre("Updated User")
                .email("updated@example.com")
                .password("newpassword")
                .build();
        
        // Mock the findById call that happens inside the controller's update method
        when(usuarioService.findById(1L)).thenReturn(usuarioDto); 
        // Mock the save call that happens after updating fields
        when(usuarioService.save(any(UsuarioDto.class))).thenReturn(updatedDto);


        mockMvc.perform(put("/usuario/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre", is(updatedDto.getNombre())))
                .andExpect(jsonPath("$.email", is(updatedDto.getEmail())));
    }
    
    @Test
    void testUpdate_InvalidData() throws Exception {
        UsuarioDto invalidDto = UsuarioDto.builder().id(1L).nombre("").email("invalid").password("123").build();
        
        // Mock the findById call that happens inside the controller's update method
        when(usuarioService.findById(1L)).thenReturn(usuarioDto); 

        mockMvc.perform(put("/usuario/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); // Assuming ControllerAdvice handles validation
    }

    @Test
    void testUsuariosCurso() throws Exception {
        when(usuarioService.findAllByIds(any(List.class))).thenReturn(Collections.singletonList(usuarioDto));

        mockMvc.perform(get("/usuario/usuarios-por-curso").param("ids", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is(usuarioDto.getNombre())));
    }
}
