package com.jovan.com.msvc_cursos.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final CursoService cursoService;

    public KafkaConsumer(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = "myTopic", groupId = "myGroup")
    public void consumeDeleteUser(String message) {
        try {
            logger.info("MSVC CURSOS: Received message: {}", message);
            List<Long> userIds = Arrays.stream(message.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
            
            userIds.forEach(id -> {
                try {
                    cursoService.eliminarUsuarioDelCursobyIdUsuario(id);
                } catch (Exception e) {
                    logger.error("Error al eliminar usuario {} del curso: {}", id, e.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Error procesando mensaje de Kafka: {}", e.getMessage());
            throw e; 
        }
    }
}