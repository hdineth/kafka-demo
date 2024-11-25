package com.example.kafkademo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public ProducerService(KafkaTemplate<String, String> kafkaTemplate, @Value("${spring.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("Message sent: " + message);
    }
}
