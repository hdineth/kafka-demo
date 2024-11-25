package com.example.kafkademo.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerPartitionPausingBackOffManager;
import org.springframework.kafka.listener.ContainerPausingBackOffHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaConsumerBackoffManager;
import org.springframework.kafka.listener.ListenerContainerPauseService;
import org.springframework.kafka.listener.ListenerContainerRegistry;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {



    @Bean
    public ConsumerFactory<Object, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(ConsumerFactory<Object, Object> consumerFactory,
        ListenerContainerRegistry registry, TaskScheduler scheduler) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        KafkaConsumerBackoffManager backOffManager = createBackOffManager(new KafkaListenerEndpointRegistry(), scheduler);
        factory.getContainerProperties()
            .setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setContainerCustomizer(container -> {
            DelayedMessageListenerAdapter<Object, Object> delayedAdapter = wrapWithDelayedMessageListenerAdapter(backOffManager, container);
            delayedAdapter.setDelayForTopic("test", Duration.ofSeconds(10));
            delayedAdapter.setDefaultDelay(Duration.ZERO);
            container.setupMessageListener(delayedAdapter);
        });
        return factory;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @SuppressWarnings("unchecked")
    private DelayedMessageListenerAdapter<Object, Object> wrapWithDelayedMessageListenerAdapter(KafkaConsumerBackoffManager backOffManager,
        ConcurrentMessageListenerContainer<Object, Object> container) {
        return new DelayedMessageListenerAdapter<>((MessageListener<Object, Object>) container.getContainerProperties()
            .getMessageListener(), backOffManager, container.getListenerId());
    }

    private ContainerPartitionPausingBackOffManager createBackOffManager(ListenerContainerRegistry registry, TaskScheduler scheduler) {
        return new ContainerPartitionPausingBackOffManager(registry,
            new ContainerPausingBackOffHandler(new ListenerContainerPauseService(registry, scheduler)));
    }



}
