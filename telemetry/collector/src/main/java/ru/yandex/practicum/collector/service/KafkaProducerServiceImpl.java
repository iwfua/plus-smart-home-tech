package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.dto.*;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.sensors}")
    private String sensorsTopic;

    @Value("${kafka.topics.hubs}")
    private String hubsTopic;

    @Override
    public void sendSensorEvent(SensorEvent event) {
        String key = event.getId();

        log.info("Sending sensor event with id {} to topic {}", key, sensorsTopic);
        kafkaTemplate.send(sensorsTopic, key, event);
    }

    @Override
    public void sendHubEvent(HubEvent event) {
        String key = event.getHubId();

        log.info("Sending hub event with id {} to topic {}", key, hubsTopic);
        kafkaTemplate.send(hubsTopic, key, event);
    }
} 