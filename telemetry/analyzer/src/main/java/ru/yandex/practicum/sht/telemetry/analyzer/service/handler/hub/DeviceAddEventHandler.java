package ru.yandex.practicum.sht.telemetry.analyzer.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.sht.telemetry.analyzer.model.Sensor;
import ru.yandex.practicum.sht.telemetry.analyzer.repository.SensorRepository;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceAddEventHandler implements HubEventHandler {

    private final SensorRepository repository;

    @Override
    public void handle(HubEventAvro hubEvent) {
        DeviceAddedEventAvro deviceEvent = (DeviceAddedEventAvro) hubEvent.getPayload();
        processDeviceAddition(hubEvent.getHubId(), deviceEvent);
    }

    private void processDeviceAddition(String hubId, DeviceAddedEventAvro deviceEvent) {
        log.info("Processing device addition event: {}", deviceEvent);

        String deviceId = deviceEvent.getId();
        boolean deviceExists = repository.existsByIdInAndHubId(
                Collections.singletonList(deviceId),
                hubId
        );

        if (!deviceExists) {
            createAndSaveSensor(hubId, deviceId);
        } else {
            log.debug("Device with id {} already exists for hub {}", deviceId, hubId);
        }
    }

    private void createAndSaveSensor(String hubId, String deviceId) {
        Sensor newSensor = Sensor.builder()
                .id(deviceId)
                .hubId(hubId)
                .build();

        repository.save(newSensor);
        log.debug("New sensor created with id: {}", deviceId);
    }

    @Override
    public String getType() {
        return DeviceAddedEventAvro.class.getName();
    }
}