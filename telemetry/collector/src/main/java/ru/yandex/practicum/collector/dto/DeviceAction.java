package ru.yandex.practicum.collector.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class DeviceAction {
    private String sensorId;
    private ActionType type;
    private Integer value;
    
    public enum ActionType {
        ACTIVATE,
        DEACTIVATE,
        INVERSE,
        SET_VALUE
    }
} 