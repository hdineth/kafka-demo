package com.example.kafkademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class KafkaConsumerProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerProducerApplication.class, args);
    }
}
