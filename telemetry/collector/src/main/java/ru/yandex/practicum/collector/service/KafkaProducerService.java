package ru.yandex.practicum.collector.service;

import ru.yandex.practicum.collector.dto.hub.HubEvent;
import ru.yandex.practicum.collector.dto.sensor.SensorEvent;

public interface KafkaProducerService {
    void sendSensorEvent(SensorEvent event);
    void sendHubEvent(HubEvent event);
} 