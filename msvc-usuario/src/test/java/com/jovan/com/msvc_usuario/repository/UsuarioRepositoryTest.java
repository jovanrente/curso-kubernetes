package com.jovan.com.msvc_usuario.repository;

import com.jovan.com.msvc_usuario.entities.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testFindAllByIds_Success() {
        // Given
        UsuarioEntity user1 = new UsuarioEntity();
        user1.setNombre("User1");
        user1.setEmail("user1@example.com");
        user1.setPassword("pass1");
        entityManager.persist(user1);

        UsuarioEntity user2 = new UsuarioEntity();
        user2.setNombre("User2");
        user2.setEmail("user2@example.com");
        user2.setPassword("pass2");
        entityManager.persist(user2);

        UsuarioEntity user3 = new UsuarioEntity();
        user3.setNombre("User3");
        user3.setEmail("user3@example.com");
        user3.setPassword("pass3");
        entityManager.persist(user3);

        entityManager.flush();

        // When
        List<UsuarioEntity> foundUsers = usuarioRepository.findAllByIds(Arrays.asList(user1.getId(), user3.getId()));

        // Then
        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).extracting(UsuarioEntity::getId).containsExactlyInAnyOrder(user1.getId(), user3.getId());
    }

    @Test
    void testFindAllByIds_SomeIdsNotFound() {
        // Given
        UsuarioEntity user1 = new UsuarioEntity();
        user1.setNombre("User1");
        user1.setEmail("user1@example.com");
        user1.setPassword("pass1");
        entityManager.persist(user1);
        entityManager.flush();

        // Non-existent ID
        Long nonExistentId = user1.getId() + 99;

        // When
        List<UsuarioEntity> foundUsers = usuarioRepository.findAllByIds(Arrays.asList(user1.getId(), nonExistentId));

        // Then
        assertThat(foundUsers).hasSize(1);
        assertThat(foundUsers).extracting(UsuarioEntity::getId).containsExactly(user1.getId());
    }

    @Test
    void testFindAllByIds_EmptyList() {
        // Given
        // No users persisted for this test specifically

        // When
        List<UsuarioEntity> foundUsers = usuarioRepository.findAllByIds(Arrays.asList());

        // Then
        assertThat(foundUsers).isEmpty();
    }
    
    @Test
    void testFindAllByIds_NoMatchingIds() {
        // Given
        UsuarioEntity user1 = new UsuarioEntity();
        user1.setNombre("User1");
        user1.setEmail("user1@example.com");
        user1.setPassword("pass1");
        entityManager.persist(user1);
        entityManager.flush();

        // When
        List<UsuarioEntity> foundUsers = usuarioRepository.findAllByIds(Arrays.asList(user1.getId() + 99, user1.getId() + 100));

        // Then
        assertThat(foundUsers).isEmpty();
    }
}
