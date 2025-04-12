package ru.yandex.practicum.collector.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ScenarioCondition {
    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private int value;
    
    public enum ConditionType {
        MOTION,
        LUMINOSITY,
        SWITCH,
        TEMPERATURE,
        CO2LEVEL,
        HUMIDITY
    }
    
    public enum ConditionOperation {
        EQUALS,
        GREATER_THAN,
        LOWER_THAN
    }
} 