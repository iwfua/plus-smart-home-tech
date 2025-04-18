package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.config.KafkaProducerProperties;
import ru.yandex.practicum.collector.dto.hub.HubEvent;
import ru.yandex.practicum.collector.dto.sensor.SensorEvent;
import ru.yandex.practicum.collector.mapper.HubEventMapper;
import ru.yandex.practicum.collector.mapper.SensorEventMapper;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final Producer<String, SpecificRecordBase> producer;
    private final KafkaProducerProperties kafkaProperties;

    public void sendSensorEvent(SensorEvent sensorEvent) {
        if (sensorEvent.getTimestamp() == null) {
            sensorEvent.setTimestamp(Instant.now());
        }

        log.info("Sending sensor event to Kafka: {}", sensorEvent);
        send(kafkaProperties.getSensorEventsTopic(),
                sensorEvent.getHubId(),
                sensorEvent.getTimestamp().toEpochMilli(),
                SensorEventMapper.toSensorEventAvro(sensorEvent));
    }

    public void sendHubEvent(HubEvent hubEvent) {
        if (hubEvent.getTimestamp() == null) {
            hubEvent.setTimestamp(Instant.now());
        }

        log.info("Sending hub event to Kafka: {}", hubEvent);
        send(kafkaProperties.getHubEventsTopic(),
                hubEvent.getHubId(),
                hubEvent.getTimestamp().toEpochMilli(),
                HubEventMapper.toHubEventAvro(hubEvent));
    }

    private void send(String topic, String key, Long timestamp, SpecificRecordBase specificRecordBase) {
        try {
            log.debug("Sending event to topic: {}, key: {}, timestamp: {}", topic, key, timestamp);
            producer.send(new ProducerRecord<>(topic, null, timestamp, key, specificRecordBase),
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send event to topic {}: {}", topic, exception.getMessage(), exception);
                        } else {
                            log.info("Event sent successfully to topic {} at partition {}, offset {}",
                                    topic, metadata.partition(), metadata.offset());
                        }
                    });
            producer.flush();
        } catch (Exception e) {
            log.error("Error sending message to topic {}: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Error sending message to Kafka", e);
        }
    }
}