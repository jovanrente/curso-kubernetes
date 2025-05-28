package com.jovan.com.msvc_cursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cursos_usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoUsuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", unique = true)
    private Long usuarioId;
} 