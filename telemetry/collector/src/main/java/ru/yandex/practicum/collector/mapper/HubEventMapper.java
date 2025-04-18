package ru.yandex.practicum.collector.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.dto.hub.ActionType;
import ru.yandex.practicum.collector.dto.hub.ConditionOperation;
import ru.yandex.practicum.collector.dto.hub.ConditionType;
import ru.yandex.practicum.collector.dto.hub.DeviceAction;
import ru.yandex.practicum.collector.dto.hub.DeviceAddedEvent;
import ru.yandex.practicum.collector.dto.hub.DeviceRemovedEvent;
import ru.yandex.practicum.collector.dto.hub.DeviceType;
import ru.yandex.practicum.collector.dto.hub.HubEvent;
import ru.yandex.practicum.collector.dto.hub.ScenarioAddedEvent;
import ru.yandex.practicum.collector.dto.hub.ScenarioCondition;
import ru.yandex.practicum.collector.dto.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.Objects;

public class HubEventMapper {

    public static HubEventAvro toHubEventAvro(HubEvent hubEvent) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp().toEpochMilli())
                .setPayload(toHubEventPayloadAvro(hubEvent))
                .build();
    }

    public static SpecificRecordBase toHubEventPayloadAvro(HubEvent hubEvent) {
        Objects.requireNonNull(hubEvent.getType(), "Hub event type cannot be null");

        return switch (hubEvent.getType()) {
            case DEVICE_ADDED -> mapDeviceAdded((DeviceAddedEvent) hubEvent);
            case DEVICE_REMOVED -> mapDeviceRemoved((DeviceRemovedEvent) hubEvent);
            case SCENARIO_ADDED -> mapScenarioAdded((ScenarioAddedEvent) hubEvent);
            case SCENARIO_REMOVED -> mapScenarioRemoved((ScenarioRemovedEvent) hubEvent);
            default -> throw new IllegalStateException("Invalid payload: " + hubEvent.getType());
        };
    }

    public static DeviceTypeAvro toDeviceTypeAvro(DeviceType deviceType) {
        return DeviceTypeAvro.valueOf(deviceType.name());
    }

    public static DeviceActionAvro toDeviceActionAvro(DeviceAction deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(toActionTypeAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build();
    }

    public static ConditionTypeAvro toConditionTypeAvro(ConditionType conditionType) {
        return ConditionTypeAvro.valueOf(conditionType.name());
    }

    public static ConditionOperationAvro toConditionOperationAvro(ConditionOperation conditionOperation) {
        return ConditionOperationAvro.valueOf(conditionOperation.name());
    }

    private static DeviceAddedEventAvro mapDeviceAdded(DeviceAddedEvent event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(toDeviceTypeAvro(event.getDeviceType()))
                .build();
    }

    private static DeviceRemovedEventAvro mapDeviceRemoved(DeviceRemovedEvent event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    private static ScenarioAddedEventAvro mapScenarioAdded(ScenarioAddedEvent event) {
        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setActions(event.getActions().stream().map(HubEventMapper::toDeviceActionAvro).toList())
                .setConditions(event.getConditions().stream().map(HubEventMapper::toScenarioConditionAvro).toList())
                .build();
    }

    private static ScenarioRemovedEventAvro mapScenarioRemoved(ScenarioRemovedEvent event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    public static ActionTypeAvro toActionTypeAvro(ActionType actionType) {
        return ActionTypeAvro.valueOf(actionType.name());
    }

    public static ScenarioConditionAvro toScenarioConditionAvro(ScenarioCondition scenarioCondition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(toConditionTypeAvro(scenarioCondition.getType()))
                .setOperation(toConditionOperationAvro(scenarioCondition.getOperation()))
                .setValue(scenarioCondition.getValue())
                .build();
    }
}