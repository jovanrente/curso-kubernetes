package com.jovan.com.msvc_cursos.entities;

import java.util.ArrayList;
import java.util.List;

import com.jovan.com.msvc_cursos.dto.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "curso_id")
    @Builder.Default
    private List<CursoUsuarioEntity> cursoUsuarios = new ArrayList<>();

    @Transient
    @Builder.Default
    private List<Usuario> usuarios = new ArrayList<>();

    public void addCursoUsuario(CursoUsuarioEntity cursoUsuario) {
        cursoUsuarios.add(cursoUsuario);
    }

    public void removeCursoUsuario(CursoUsuarioEntity cursoUsuario) {
        cursoUsuarios.remove(cursoUsuario);
    }
}
