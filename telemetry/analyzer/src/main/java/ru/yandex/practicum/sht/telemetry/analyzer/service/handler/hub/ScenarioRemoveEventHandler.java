package ru.yandex.practicum.sht.telemetry.analyzer.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.sht.telemetry.analyzer.repository.ScenarioRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScenarioRemoveEventHandler implements HubEventHandler {

    private final ScenarioRepository scenarioStore;

    @Override
    public void handle(HubEventAvro hubEvent) {
        ScenarioRemovedEventAvro scenarioEvent = (ScenarioRemovedEventAvro) hubEvent.getPayload();
        deleteScenario(hubEvent.getHubId(), scenarioEvent);
    }

    private void deleteScenario(String hubIdentifier, ScenarioRemovedEventAvro scenarioData) {
        String scenarioName = scenarioData.getName();
        log.info("Handling scenario deletion request for: {}", scenarioName);

        scenarioStore.findByHubIdAndName(hubIdentifier, scenarioName)
                .ifPresentOrElse(
                        scenario -> {
                            scenarioStore.delete(scenario);
                            log.debug("Successfully removed scenario: {}", scenarioName);
                        },
                        () -> log.warn("Scenario not found: {} for hub: {}",
                                scenarioName, hubIdentifier)
                );
    }

    @Override
    public String getType() {
        return ScenarioRemovedEventAvro.class.getName();
    }
}