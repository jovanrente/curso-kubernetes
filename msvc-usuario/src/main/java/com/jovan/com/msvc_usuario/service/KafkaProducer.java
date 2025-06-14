package com.jovan.com.msvc_usuario.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.jovan.com.msvc_usuario.entities.KafkaFallbackEvent;
import com.jovan.com.msvc_usuario.repository.KafkaFallbackEventRepository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 30000;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaFallbackEventRepository fallbackRepo;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate, KafkaFallbackEventRepository fallbackRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.fallbackRepo = fallbackRepo;
    }


    @Retryable(
        value = {KafkaException.class},
        maxAttempts = MAX_RETRY_ATTEMPTS,
        backoff = @Backoff(delay = RETRY_DELAY_MS, multiplier = 2.0)
    )
    public void sendDeleteUser(String topic, List<Long> userIds) {
        String message = String.join(",", userIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Mensaje de eliminación enviado exitosamente al tópico: {} - Offset: {}", 
                        topic, result.getRecordMetadata().offset());
                } else {
                    logger.error("Error al enviar mensaje de eliminación a Kafka - Tópico: {} - Error: {}", 
                        topic, ex.getMessage());
                    throw new KafkaException("Error al enviar mensaje de eliminación", ex);
                }
            });
        } catch (KafkaException e) {
            logger.error("Error al enviar mensaje de eliminación a Kafka: {}", e.getMessage());
            throw e;
        }
    }


    @Recover
    public void recoverSendDeleteUser(KafkaException e, String topic, List<Long> userIds) {
        String message = String.join(",", userIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        logger.error("Recuperación después de reintentos fallidos para el tópico: {} - Error: {}", topic, e.getMessage());
        handleKafkaError(topic, message, e);
    }

    private void handleKafkaError(String topic, String payload, Throwable throwable) {
        try {
            KafkaFallbackEvent event = new KafkaFallbackEvent();
            event.setTopic(topic);
            event.setPayload(payload);
            event.setRetryCount(0);
            event.setError(throwable.getMessage());
            event.setLastRetryAt(LocalDateTime.now());
            fallbackRepo.save(event);
            logger.warn("Evento guardado en fallback - Tópico: {} - Error: {}", topic, throwable.getMessage());
        } catch (Exception e) {
            logger.error("Error al guardar fallback de Kafka: {}", e.getMessage());
        }
    }
}
 