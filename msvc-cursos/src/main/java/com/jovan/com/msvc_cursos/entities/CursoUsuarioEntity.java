package com.jovan.com.msvc_cursos.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cursos_usuarios")    
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoUsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", unique = true) 
    private Long usuarioId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CursoUsuarioEntity)) return false;
        CursoUsuarioEntity that = (CursoUsuarioEntity) o;
        return this.usuarioId != null && this.usuarioId.equals(that.usuarioId);
    }

    
}
