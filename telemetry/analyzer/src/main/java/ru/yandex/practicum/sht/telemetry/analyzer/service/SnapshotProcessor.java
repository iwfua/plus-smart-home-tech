package ru.yandex.practicum.sht.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.sht.telemetry.analyzer.config.KafkaConfig;
import ru.yandex.practicum.sht.telemetry.analyzer.config.KafkaConfig.TopicType;
import ru.yandex.practicum.sht.telemetry.analyzer.service.handler.snapshot.SensorsSnapshotHandler;

import java.time.Duration;
import java.util.*;

import static ru.yandex.practicum.sht.telemetry.analyzer.config.KafkaConfig.TopicType.SNAPSHOTS_EVENTS;

@Component
@Slf4j
public class SnapshotProcessor {

    private static final Duration POLL_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration SHUTDOWN_TIMEOUT = Duration.ofMillis(10);

    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final EnumMap<TopicType, String> topicConfiguration;
    private final Map<TopicPartition, OffsetAndMetadata> offsetsTracker;
    private final SensorsSnapshotHandler handler;

    public SnapshotProcessor(KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer,
                             KafkaConfig kafkaConfig,
                             SensorsSnapshotHandler handler) {
        this.snapshotConsumer = snapshotConsumer;
        this.topicConfiguration = kafkaConfig.getSnapshotConsumer().getTopics();
        this.offsetsTracker = new HashMap<>();
        this.handler = handler;
    }

    public void start() {
        try {
            configureConsumer();
            mainProcessingLoop();
        } catch (Exception e) {
            log.error("Error processing snapshots: {}", e.getMessage(), e);
        } finally {
            cleanupResources();
        }
    }

    private void configureConsumer() {
        Runtime.getRuntime().addShutdownHook(new Thread(snapshotConsumer::wakeup));
        String targetTopic = topicConfiguration.get(SNAPSHOTS_EVENTS);
        snapshotConsumer.subscribe(Collections.singletonList(targetTopic));
    }

    private void mainProcessingLoop() {
        while (true) {
            ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotConsumer.poll(POLL_TIMEOUT);
            for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                processRecord(record);
            }
            snapshotConsumer.commitAsync();
        }
    }

    private void processRecord(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        log.info("topic = {}, partition = {}, offset = {}, record = {}",
                record.topic(), record.partition(), record.offset(), record.value());
        handler.handle(record.value());
        trackOffset(record);
    }

    private void trackOffset(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        TopicPartition partition = new TopicPartition(record.topic(), record.partition());
        OffsetAndMetadata newOffset = new OffsetAndMetadata(record.offset() + 1);
        offsetsTracker.put(partition, newOffset);
    }

    private void cleanupResources() {
        try {
            snapshotConsumer.commitSync(offsetsTracker);
        } finally {
            snapshotConsumer.close(SHUTDOWN_TIMEOUT);
        }
    }
}