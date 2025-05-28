package com.jovan.com.msvc_usuario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jovan.com.msvc_usuario.entities.KafkaFallbackEvent;

public interface KafkaFallbackEventRepository extends JpaRepository<KafkaFallbackEvent, Long> {
    List<KafkaFallbackEvent> findBySentFalse();
}
