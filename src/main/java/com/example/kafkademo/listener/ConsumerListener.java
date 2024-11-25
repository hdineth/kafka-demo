package com.example.kafkademo.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.listener.KafkaBackoffException;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsumerListener {

    @RetryableTopic(attempts = "1", include = KafkaBackoffException.class, dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(id = "myListenerId", topics = { "test",}, groupId = "my-group")
    public void listen(String message) {
        log.info("Received message: {}",message);
    }
}
