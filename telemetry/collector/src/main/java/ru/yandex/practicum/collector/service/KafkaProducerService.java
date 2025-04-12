package ru.yandex.practicum.collector.service;

import ru.yandex.practicum.collector.dto.HubEvent;
import ru.yandex.practicum.collector.dto.SensorEvent;

public interface KafkaProducerService {
    void sendSensorEvent(SensorEvent event);
    void sendHubEvent(HubEvent event);
} 