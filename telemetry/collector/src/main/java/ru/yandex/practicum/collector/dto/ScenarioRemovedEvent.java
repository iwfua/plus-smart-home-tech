package ru.yandex.practicum.collector.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    @NotNull
    @Size(min = 3)
    private String name;

    @Override
    @NotNull
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
} 