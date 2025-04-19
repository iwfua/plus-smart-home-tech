package ru.yandex.practicum.sht.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.sht.telemetry.analyzer.config.KafkaConfig;
import ru.yandex.practicum.sht.telemetry.analyzer.config.KafkaConfig.TopicType;
import ru.yandex.practicum.sht.telemetry.analyzer.service.handler.hub.HubEventHandler;

import static ru.yandex.practicum.sht.telemetry.analyzer.config.KafkaConfig.TopicType.HUBS_EVENTS;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HubEventProcessor implements Runnable {

    private static final Duration POLLING_DURATION = Duration.ofSeconds(5);
    private static final Duration TERMINATION_TIMEOUT = Duration.ofMillis(10);

    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final EnumMap<TopicType, String> topicsMap;
    private final Map<TopicPartition, OffsetAndMetadata> offsetMap;
    private final Map<String, HubEventHandler> handlerMap;

    public HubEventProcessor(KafkaConsumer<String, HubEventAvro> consumer, KafkaConfig kafkaConfig, Set<HubEventHandler> handlers) {
        this.consumer = consumer;
        this.topicsMap = kafkaConfig.getHubConsumer().getTopics();
        this.offsetMap = new HashMap<>();
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getType,
                        Function.identity()
                ));
    }

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(Collections.singletonList(topicsMap.get(HUBS_EVENTS)));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(POLLING_DURATION);
                processRecords(records);
                consumer.commitAsync();
            }
        } catch (Exception e) {
            log.error("Error processing hub events: {}", e.getMessage(), e);
        } finally {
            finalizeConsumer();
        }
    }

    private void processRecords(ConsumerRecords<String, HubEventAvro> records) {
        for (ConsumerRecord<String, HubEventAvro> record : records) {
            processRecord(record);
        }
    }

    private void processRecord(ConsumerRecord<String, HubEventAvro> record) {
        String payloadClassName = record.value().getPayload().getClass().getName();
        HubEventHandler handler = handlerMap.get(payloadClassName);

        if (handler != null) {
            handler.handle(record.value());
        }

        updateOffset(record);
    }

    private void updateOffset(ConsumerRecord<String, HubEventAvro> record) {
        TopicPartition partition = new TopicPartition(record.topic(), record.partition());
        OffsetAndMetadata offsetMeta = new OffsetAndMetadata(record.offset() + 1);
        offsetMap.put(partition, offsetMeta);
    }

    private void finalizeConsumer() {
        try {
            consumer.commitSync(offsetMap);
        } finally {
            consumer.close(TERMINATION_TIMEOUT);
        }
    }
}