package ru.yandex.practicum.collector.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public enum ActionType {
    ACTIVATE,
    DEACTIVATE,
    INVERSE,
    SET_VALUE
}