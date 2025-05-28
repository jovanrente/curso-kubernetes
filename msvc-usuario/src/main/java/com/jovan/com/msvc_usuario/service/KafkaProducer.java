package com.jovan.com.msvc_usuario.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jovan.com.msvc_usuario.entities.KafkaFallbackEvent;
import com.jovan.com.msvc_usuario.repository.KafkaFallbackEventRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

    public void sendMessage(String topic, String message) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Mensaje enviado exitosamente al tópico: {} - Offset: {}", 
                        topic, result.getRecordMetadata().offset());
                } else {
                    logger.error("Error al enviar mensaje a Kafka - Tópico: {} - Error: {}", 
                        topic, ex.getMessage());
                    handleKafkaError(topic, message, ex);
                }
            });
        } catch (KafkaException e) {
            logger.error("Error al enviar mensaje a Kafka: {}", e.getMessage());
            handleKafkaError(topic, message, e);
        }
    }

    @CircuitBreaker(name = "kafkaBreaker", fallbackMethod = "fallbackSendDeleteUser")
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
                    handleKafkaError(topic, message, ex);
                }
            });
        } catch (KafkaException e) {
            logger.error("Error al enviar mensaje de eliminación a Kafka: {}", e.getMessage());
            handleKafkaError(topic, message, e);
        }
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

    public void fallbackSendDeleteUser(String topic, String payload, Throwable throwable) {
        handleKafkaError(topic, payload, throwable);
    }

    @Transactional
    @Scheduled(fixedRate = RETRY_DELAY_MS)
    public void retryFailedEvents() {
        List<KafkaFallbackEvent> events = fallbackRepo.findBySentFalse();
        for (KafkaFallbackEvent event : events) {
            if (event.getRetryCount() >= MAX_RETRY_ATTEMPTS) {
                logger.warn("Evento excedió el número máximo de reintentos - Tópico: {} - Payload: {}", 
                    event.getTopic(), event.getPayload());
                event.setSent(true);
                event.setError("Excedido número máximo de reintentos");
                fallbackRepo.save(event);
                continue;
            }

            try {
                CompletableFuture<SendResult<String, Object>> future = 
                    kafkaTemplate.send(event.getTopic(), event.getPayload());
                
                future.whenComplete((result, ex) -> {
                    if (ex == null) {
                        event.setSent(true);
                        event.setLastRetryAt(LocalDateTime.now());
                        fallbackRepo.save(event);
                        logger.info("Evento reenviado exitosamente a Kafka - Tópico: {} - Offset: {}", 
                            event.getTopic(), result.getRecordMetadata().offset());
                    } else {
                        event.setRetryCount(event.getRetryCount() + 1);
                        event.setError(ex.getMessage());
                        event.setLastRetryAt(LocalDateTime.now());
                        fallbackRepo.save(event);
                        logger.error("Error reintentando evento - Tópico: {} - Intento: {} - Error: {}", 
                            event.getTopic(), event.getRetryCount(), ex.getMessage());
                    }
                });
            } catch (KafkaException e) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setError(e.getMessage());
                event.setLastRetryAt(LocalDateTime.now());
                fallbackRepo.save(event);
                logger.error("Error reintentando evento - Tópico: {} - Intento: {} - Error: {}", 
                    event.getTopic(), event.getRetryCount(), e.getMessage());
            }
        }
    }
}
 