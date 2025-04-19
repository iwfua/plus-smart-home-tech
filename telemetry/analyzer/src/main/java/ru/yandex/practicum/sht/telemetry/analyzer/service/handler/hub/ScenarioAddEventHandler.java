package ru.yandex.practicum.sht.telemetry.analyzer.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.sht.telemetry.analyzer.model.*;
import ru.yandex.practicum.sht.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.sht.telemetry.analyzer.repository.SensorRepository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScenarioAddEventHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepo;
    private final SensorRepository sensorRepo;

    @Override
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioEvent = (ScenarioAddedEventAvro) event.getPayload();
        log.info("Processing scenario creation: {}", scenarioEvent.getName());

        String hubId = event.getHubId();
        String scenarioName = scenarioEvent.getName();

        if (scenarioRepo.findByHubIdAndName(hubId, scenarioName).isEmpty()) {
            createAndSaveScenario(hubId, scenarioEvent);
        } else {
            log.info("Scenario already exists: {} for hub: {}", scenarioName, hubId);
        }
    }

    private void createAndSaveScenario(String hubId, ScenarioAddedEventAvro scenarioData) {
        try {
            Scenario newScenario = Scenario.builder()
                    .hubId(hubId)
                    .name(scenarioData.getName())
                    .conditions(convertConditions(scenarioData.getConditions(), hubId))
                    .actions(convertActions(scenarioData.getActions(), hubId))
                    .build();

            scenarioRepo.save(newScenario);
            log.debug("Scenario created successfully: {}", scenarioData.getName());
        } catch (NoSuchElementException e) {
            log.error("Cannot create scenario - sensors not found", e);
        }
    }

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getName();
    }

    private Map<String, Condition> convertConditions(List<ScenarioConditionAvro> sourceConditions, String hubId) {
        List<String> sensorIds = extractSensorIds(sourceConditions, ScenarioConditionAvro::getSensorId);

        if (!sensorRepo.existsByIdInAndHubId(sensorIds, hubId)) {
            throw new NoSuchElementException("Required sensors not found for conditions");
        }

        return sourceConditions.stream()
                .collect(Collectors.toMap(
                        ScenarioConditionAvro::getSensorId,
                        this::buildConditionFromAvro
                ));
    }

    private Condition buildConditionFromAvro(ScenarioConditionAvro source) {
        return Condition.builder()
                .type(source.getType())
                .operation(source.getOperation())
                .value(convertToNumericValue(source.getValue()))
                .build();
    }

    private Integer convertToNumericValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        return 0;
    }

    private Map<String, Action> convertActions(List<DeviceActionAvro> sourceActions, String hubId) {
        List<String> sensorIds = extractSensorIds(sourceActions, DeviceActionAvro::getSensorId);

        if (!sensorRepo.existsByIdInAndHubId(sensorIds, hubId)) {
            throw new NoSuchElementException("Required sensors not found for actions");
        }

        return sourceActions.stream()
                .collect(Collectors.toMap(
                        DeviceActionAvro::getSensorId,
                        this::buildActionFromAvro
                ));
    }

    private Action buildActionFromAvro(DeviceActionAvro source) {
        return Action.builder()
                .type(source.getType())
                .value(source.getValue() != null ? source.getValue() : 0)
                .build();
    }

    private <T> List<String> extractSensorIds(List<T> items, Function<T, String> idExtractor) {
        return items.stream()
                .map(idExtractor)
                .collect(Collectors.toList());
    }
}