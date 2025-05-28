package com.jovan.com.msvc_usuario.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jovan.com.msvc_usuario.service.KafkaProducer;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducer kafkaProducer;

    public KafkaController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam("message") String message) {

       // kafkaProducer.sendMessage("myTopic", message);
       kafkaProducer.sendDeleteUser("myTopic", List.of(1L, 2L, 3L));

        return ResponseEntity.ok("Message sent to Kafka");
    }
    
}
