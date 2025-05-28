package com.jovan.com.msvc_usuario.entities;

import java.time.LocalDateTime;

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
@Table(name = "kafka_fallback_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaFallbackEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Column(columnDefinition = "TEXT")
    private String payload; // Guarda el JSON de los IDs

    private boolean sent = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(columnDefinition = "TEXT")
    private String error;

    private LocalDateTime lastRetryAt;

}