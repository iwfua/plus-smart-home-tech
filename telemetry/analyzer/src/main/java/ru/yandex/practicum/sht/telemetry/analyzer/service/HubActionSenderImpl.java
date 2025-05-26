package ru.yandex.practicum.sht.telemetry.analyzer.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.sht.telemetry.analyzer.model.Action;

import java.time.Instant;

@Service
@Slf4j
public class HubActionSenderImpl implements HubActionSender {

    private final HubRouterControllerBlockingStub routerClient;

    public HubActionSenderImpl(@GrpcClient("hub-router") HubRouterControllerBlockingStub routerClient) {
        this.routerClient = routerClient;
    }

    public void sendAction(String hubId, String scenarioName, String sensorId, Action action) {
        try {
            log.info("Sending {} action to hub: {}, sensor: {}",
                    action.getType().name(), hubId, sensorId);
            routerClient.handleDeviceAction(createActionRequest(hubId, scenarioName, sensorId, action));
        } catch (Exception e) {
            log.error("Failed to send action: {}", e.getMessage(), e);
        }
    }

    private DeviceActionRequest createActionRequest(String hubId, String scenarioName,
                                                    String sensorId, Action action) {
        Instant currentTime = Instant.now();

        DeviceActionProto actionProto = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(ActionTypeProto.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();

        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(currentTime.getEpochSecond())
                .setNanos(currentTime.getNano())
                .build();

        return DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(actionProto)
                .setTimestamp(timestamp)
                .build();
    }
}