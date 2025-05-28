package com.jovan.com.msvc_usuario.service;

import com.jovan.com.msvc_usuario.dto.UsuarioDto;
import com.jovan.com.msvc_usuario.entities.UsuarioEntity;
import com.jovan.com.msvc_usuario.exception.RequestException;
import com.jovan.com.msvc_usuario.mapper.UsuarioMapper;
import com.jovan.com.msvc_usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsusuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private UsusuarioServiceImpl usuarioService;

    private UsuarioEntity usuarioEntity;
    private UsuarioDto usuarioDto;

    @BeforeEach
    void setUp() {
        usuarioEntity = new UsuarioEntity();
        usuarioEntity.setId(1L);
        usuarioEntity.setNombre("Test User");
        usuarioEntity.setEmail("test@example.com");
        usuarioEntity.setPassword("password");

        usuarioDto = UsuarioDto.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    void testFindAll() {
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuarioEntity));
        List<UsuarioDto> result = usuarioService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(usuarioDto.getNombre(), result.get(0).getNombre());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEntity));
        UsuarioDto result = usuarioService.findById(1L);
        assertNotNull(result);
        assertEquals(usuarioDto.getId(), result.getId());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        RequestException exception = assertThrows(RequestException.class, () -> {
            usuarioService.findById(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("El usuario con el id 1 no existe"));
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        // Mocking the static method toUsuarioEntity from UsuarioMapper is tricky without PowerMock or changing the code.
        // Assuming UsuarioMapper.toUsuarioEntity and UsuarioMapper.toUsuarioDto work as expected.
        // If UsuarioMapper itself needs testing, it should have its own unit test.
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);
        UsuarioDto result = usuarioService.save(usuarioDto);
        assertNotNull(result);
        assertEquals(usuarioDto.getNombre(), result.getNombre());
        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
    }

    @Test
    void testDelete_Success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEntity));
        doNothing().when(usuarioRepository).deleteById(1L);
        doNothing().when(kafkaProducer).sendDeleteUser(eq("myTopic"), anyList());

        usuarioService.delete(1L);

        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
        verify(kafkaProducer, times(1)).sendDeleteUser("myTopic", List.of(1L));
    }

    @Test
    void testDelete_NotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        RequestException exception = assertThrows(RequestException.class, () -> {
            usuarioService.delete(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("El usuario con el id 1 no existe"));
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, never()).deleteById(anyLong());
        verify(kafkaProducer, never()).sendDeleteUser(anyString(), anyList());
    }

    @Test
    void testFindAllByIds() {
        when(usuarioRepository.findAllById(anyList())).thenReturn(Collections.singletonList(usuarioEntity));
        List<Long> ids = List.of(1L);
        List<UsuarioDto> result = usuarioService.findAllByIds(ids);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(usuarioDto.getNombre(), result.get(0).getNombre());
        verify(usuarioRepository, times(1)).findAllById(ids);
    }
}
