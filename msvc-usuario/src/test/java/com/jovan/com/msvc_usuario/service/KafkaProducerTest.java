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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private KafkaFallbackEventRepository fallbackRepo;

    @Mock
    private SendResult<String, Object> sendResult; // Mocking SendResult

    @InjectMocks
    private KafkaProducer kafkaProducer;

    private CompletableFuture<SendResult<String, Object>> completableFuture;

    @BeforeEach
    void setUp() {
        // Mock the CompletableFuture that KafkaTemplate.send() returns
        completableFuture = CompletableFuture.completedFuture(sendResult); 
    }

    @Test
    void testSendMessage_Success() {
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(completableFuture);

        kafkaProducer.sendMessage("testTopic", "testMessage");

        // Verify that kafkaTemplate.send was called
        verify(kafkaTemplate, times(1)).send("testTopic", "testMessage");
        // We can also add a small delay or use ArgumentCaptor if we need to verify async callbacks,
        // but for now, just verifying the send call is a good start.
    }

    @Test
    void testSendMessage_KafkaException() {
        // Simulate KafkaException during send
        when(kafkaTemplate.send(anyString(), anyString())).thenThrow(new org.springframework.kafka.KafkaException("Test Kafka Exception"));
        
        kafkaProducer.sendMessage("testTopic", "testMessageError");

        verify(kafkaTemplate, times(1)).send("testTopic", "testMessageError");
        // Verify fallbackRepo.save is called
        ArgumentCaptor<KafkaFallbackEvent> eventCaptor = ArgumentCaptor.forClass(KafkaFallbackEvent.class);
        verify(fallbackRepo, times(1)).save(eventCaptor.capture());
        
        KafkaFallbackEvent savedEvent = eventCaptor.getValue();
        assertEquals("testTopic", savedEvent.getTopic());
        assertEquals("testMessageError", savedEvent.getPayload());
        assertNotNull(savedEvent.getError());
        assertEquals(0, savedEvent.getRetryCount());
    }
    
    @Test
    void testSendMessage_FutureCompletesExceptionally() {
        // Simulate the CompletableFuture completing with an exception
        completableFuture = new CompletableFuture<>();
        completableFuture.completeExceptionally(new RuntimeException("Async send error"));
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(completableFuture);

        kafkaProducer.sendMessage("testTopic", "testMessageAsyncError");

        verify(kafkaTemplate, times(1)).send("testTopic", "testMessageAsyncError");
        
        // It might take a moment for the exceptionally completed future to trigger the callback.
        // For robust testing of callbacks, awaitility or similar might be needed.
        // Here, we'll verify the fallbackRepo.save with a small delay, acknowledging this might be flaky.
        // A better approach for real-world scenarios would be to refactor for testability or use dedicated async testing tools.
        try {
            Thread.sleep(100); // Small delay to allow CompletableFuture callback
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ArgumentCaptor<KafkaFallbackEvent> eventCaptor = ArgumentCaptor.forClass(KafkaFallbackEvent.class);
        verify(fallbackRepo, times(1)).save(eventCaptor.capture());
        assertEquals("testTopic", eventCaptor.getValue().getTopic());
        assertEquals("testMessageAsyncError", eventCaptor.getValue().getPayload());
    }


    @Test
    void testSendDeleteUser_Success() {
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(completableFuture);
        List<Long> userIds = List.of(1L, 2L);
        
        kafkaProducer.sendDeleteUser("deleteTopic", userIds);

        verify(kafkaTemplate, times(1)).send("deleteTopic", "1,2");
    }

    @Test
    void testSendDeleteUser_KafkaException() {
        List<Long> userIds = List.of(3L, 4L);
        String expectedPayload = "3,4";
        when(kafkaTemplate.send("deleteTopicError", expectedPayload))
            .thenThrow(new org.springframework.kafka.KafkaException("Test Kafka Exception for delete"));

        kafkaProducer.sendDeleteUser("deleteTopicError", userIds);

        verify(kafkaTemplate, times(1)).send("deleteTopicError", expectedPayload);
        ArgumentCaptor<KafkaFallbackEvent> eventCaptor = ArgumentCaptor.forClass(KafkaFallbackEvent.class);
        verify(fallbackRepo, times(1)).save(eventCaptor.capture());
        
        KafkaFallbackEvent savedEvent = eventCaptor.getValue();
        assertEquals("deleteTopicError", savedEvent.getTopic());
        assertEquals(expectedPayload, savedEvent.getPayload());
    }
    
    @Test
    void testSendDeleteUser_FutureCompletesExceptionally() {
        List<Long> userIds = List.of(5L, 6L);
        String expectedPayload = "5,6";
        completableFuture = new CompletableFuture<>();
        completableFuture.completeExceptionally(new RuntimeException("Async delete error"));
        when(kafkaTemplate.send("deleteTopicAsyncError", expectedPayload)).thenReturn(completableFuture);

        kafkaProducer.sendDeleteUser("deleteTopicAsyncError", userIds);

        verify(kafkaTemplate, times(1)).send("deleteTopicAsyncError", expectedPayload);
        
        try {
            Thread.sleep(100); // Small delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ArgumentCaptor<KafkaFallbackEvent> eventCaptor = ArgumentCaptor.forClass(KafkaFallbackEvent.class);
        verify(fallbackRepo, times(1)).save(eventCaptor.capture());
        assertEquals("deleteTopicAsyncError", eventCaptor.getValue().getTopic());
        assertEquals(expectedPayload, eventCaptor.getValue().getPayload());
    }

    // Note: Testing the @Scheduled retryFailedEvents and @CircuitBreaker fallbackMethod 
    // would typically require Spring integration testing context (to enable scheduling and AOP)
    // or more advanced mocking techniques (e.g., with PowerMock or by refactoring the code
    // to make time and async operations more controllable).
    // These tests focus on the direct logic of the send methods.
}
