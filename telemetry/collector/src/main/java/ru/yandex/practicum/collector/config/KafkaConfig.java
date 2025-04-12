package ru.yandex.practicum.collector.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${kafka.topics.sensors}")
    private String sensorsTopic;

    @Value("${kafka.topics.hubs}")
    private String hubsTopic;

    @Bean
    public NewTopic sensorsTopic() {
        return TopicBuilder.name(sensorsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic hubsTopic() {
        return TopicBuilder.name(hubsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
} 