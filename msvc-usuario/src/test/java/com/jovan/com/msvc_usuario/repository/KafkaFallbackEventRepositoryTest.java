package com.jovan.com.msvc_usuario.repository;

import com.jovan.com.msvc_usuario.entities.KafkaFallbackEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class KafkaFallbackEventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private KafkaFallbackEventRepository kafkaFallbackEventRepository;

    @Test
    void testFindBySentFalse_SomeEventsUnsent() {
        // Given
        KafkaFallbackEvent event1 = new KafkaFallbackEvent();
        event1.setTopic("topic1");
        event1.setPayload("payload1");
        event1.setSent(false);
        event1.setRetryCount(0);
        event1.setLastRetryAt(LocalDateTime.now());
        entityManager.persist(event1);

        KafkaFallbackEvent event2 = new KafkaFallbackEvent();
        event2.setTopic("topic2");
        event2.setPayload("payload2");
        event2.setSent(true); // This one is marked as sent
        event2.setRetryCount(1);
        event2.setLastRetryAt(LocalDateTime.now());
        entityManager.persist(event2);

        KafkaFallbackEvent event3 = new KafkaFallbackEvent();
        event3.setTopic("topic3");
        event3.setPayload("payload3");
        event3.setSent(false);
        event3.setRetryCount(2);
        event3.setLastRetryAt(LocalDateTime.now());
        entityManager.persist(event3);

        entityManager.flush();

        // When
        List<KafkaFallbackEvent> unsentEvents = kafkaFallbackEventRepository.findBySentFalse();

        // Then
        assertThat(unsentEvents).hasSize(2);
        assertThat(unsentEvents).extracting(KafkaFallbackEvent::getPayload)
                                 .containsExactlyInAnyOrder("payload1", "payload3");
        assertThat(unsentEvents).allMatch(event -> !event.isSent());
    }

    @Test
    void testFindBySentFalse_AllEventsSent() {
        // Given
        KafkaFallbackEvent event1 = new KafkaFallbackEvent();
        event1.setTopic("topic1");
        event1.setPayload("payload1");
        event1.setSent(true);
        event1.setRetryCount(0);
        event1.setLastRetryAt(LocalDateTime.now());
        entityManager.persist(event1);

        KafkaFallbackEvent event2 = new KafkaFallbackEvent();
        event2.setTopic("topic2");
        event2.setPayload("payload2");
        event2.setSent(true);
        event2.setRetryCount(1);
        event2.setLastRetryAt(LocalDateTime.now());
        entityManager.persist(event2);

        entityManager.flush();

        // When
        List<KafkaFallbackEvent> unsentEvents = kafkaFallbackEventRepository.findBySentFalse();

        // Then
        assertThat(unsentEvents).isEmpty();
    }

    @Test
    void testFindBySentFalse_NoEvents() {
        // Given
        // No events persisted

        // When
        List<KafkaFallbackEvent> unsentEvents = kafkaFallbackEventRepository.findBySentFalse();

        // Then
        assertThat(unsentEvents).isEmpty();
    }
}
