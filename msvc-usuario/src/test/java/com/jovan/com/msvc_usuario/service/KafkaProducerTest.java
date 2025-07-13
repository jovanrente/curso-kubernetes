package com.jovan.com.msvc_usuario.service;

import com.jovan.com.msvc_usuario.entities.KafkaFallbackEvent;
import com.jovan.com.msvc_usuario.repository.KafkaFallbackEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private KafkaFallbackEventRepository fallbackRepo;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    private CompletableFuture<SendResult<String, Object>> completableFuture;

    @BeforeEach
    void setUp() {
        completableFuture = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(completableFuture);
    }

    @Test
    void testSendDeleteUser_Success() throws Exception {
        List<Long> userIds = List.of(1L, 2L);
        SendResult<String, Object> sendResult = mock(SendResult.class);
        completableFuture.complete(sendResult);

        CompletableFuture<Void> future = kafkaProducer.sendDeleteUser("deleteTopic", userIds);

        future.get(5, TimeUnit.SECONDS);
        verify(kafkaTemplate, times(1)).send("deleteTopic", "1,2");
    }

    @Test
    void testSendDeleteUser_WithRetry() throws Exception {
        List<Long> userIds = List.of(3L, 4L);
        when(kafkaTemplate.send("deleteTopic", "3,4"))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Error temporal")));

        CompletableFuture<Void> future = kafkaProducer.sendDeleteUser("deleteTopic", userIds);

        future.get(10, TimeUnit.SECONDS);

        // Solo verificamos que el fallback se ejecutó
        ArgumentCaptor<KafkaFallbackEvent> eventCaptor = ArgumentCaptor.forClass(KafkaFallbackEvent.class);
        verify(fallbackRepo, times(1)).save(eventCaptor.capture());
        KafkaFallbackEvent savedEvent = eventCaptor.getValue();
        assertEquals("deleteTopic", savedEvent.getTopic());
        assertEquals("3,4", savedEvent.getPayload());
        assertNotNull(savedEvent.getError());
    }

    @Test
    void testSendDeleteUser_MaxRetriesExceeded() throws Exception {
        List<Long> userIds = List.of(5L, 6L);
        when(kafkaTemplate.send("deleteTopic", "5,6"))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Error persistente")));

        CompletableFuture<Void> future = kafkaProducer.sendDeleteUser("deleteTopic", userIds);

        future.get(15, TimeUnit.SECONDS);

        ArgumentCaptor<KafkaFallbackEvent> eventCaptor = ArgumentCaptor.forClass(KafkaFallbackEvent.class);
        verify(fallbackRepo, times(1)).save(eventCaptor.capture());

        KafkaFallbackEvent savedEvent = eventCaptor.getValue();
        assertEquals("deleteTopic", savedEvent.getTopic());
        assertEquals("5,6", savedEvent.getPayload());
        assertNotNull(savedEvent.getError());
    }
} 