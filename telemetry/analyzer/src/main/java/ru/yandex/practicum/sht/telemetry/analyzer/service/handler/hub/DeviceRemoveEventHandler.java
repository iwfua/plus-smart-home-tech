package ru.yandex.practicum.sht.telemetry.analyzer.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.sht.telemetry.analyzer.repository.SensorRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceRemoveEventHandler implements HubEventHandler {

    private final SensorRepository sensorStore;

    @Override
    public void handle(HubEventAvro event) {
        DeviceRemovedEventAvro deviceEvent = (DeviceRemovedEventAvro) event.getPayload();
        removeDeviceIfExists(event.getHubId(), deviceEvent);
    }

    private void removeDeviceIfExists(String hubIdentifier, DeviceRemovedEventAvro deviceEvent) {
        String deviceId = deviceEvent.getId();
        log.info("Processing device removal request for device: {}", deviceId);

        sensorStore.findByIdAndHubId(deviceId, hubIdentifier)
                .ifPresentOrElse(
                        sensor -> {
                            sensorStore.delete(sensor);
                            log.debug("Device successfully removed: {}", deviceId);
                        },
                        () -> log.debug("Device not found: {} in hub: {}", deviceId, hubIdentifier)
                );
    }

    @Override
    public String getType() {
        return DeviceRemovedEventAvro.class.getName();
    }
}