package com.jovan.com.msvc_usuario.controller; // Singular

import com.jovan.com.msvc_usuario.controllers.KafkaController; // Import from plural
import com.jovan.com.msvc_usuario.service.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KafkaController.class) // Refers to imported KafkaController
public class KafkaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaProducer kafkaProducer;

    @Test
    void testSendMessage() throws Exception {
        String messageParam = "test-message"; // This param is not actually used by the controller method as per current code
        List<Long> expectedIdList = List.of(1L, 2L, 3L); // Hardcoded in controller

        mockMvc.perform(post("/kafka/send")
                .param("message", messageParam))
                .andExpect(status().isOk())
                .andExpect(content().string("Message sent to Kafka"));

        verify(kafkaProducer, times(1)).sendDeleteUser("myTopic", expectedIdList);
    }
}
