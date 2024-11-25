package com.example.kafkademo.controller;

import com.example.kafkademo.service.ProducerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {
    private final ProducerService producerService;

    public KafkaController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/publish")
    public String publishMessage(@RequestParam("message") String message) {
        producerService.sendMessage(message);
        return "Message sent successfully!";
    }
}
