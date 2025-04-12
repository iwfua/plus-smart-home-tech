package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.dto.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.sensors}")
    private String sensorsTopic;

    @Value("${kafka.topics.hubs}")
    private String hubsTopic;

    @Override
    public void sendSensorEvent(SensorEvent event) {
        String key = event.getId();
        SensorEventAvro avroEvent = convertToAvro(event);
        
        log.info("Sending sensor event with id {} to topic {}", key, sensorsTopic);
        kafkaTemplate.send(sensorsTopic, key, avroEvent);
    }

    @Override
    public void sendHubEvent(HubEvent event) {
        String key = event.getHubId();
        HubEventAvro avroEvent = convertToAvro(event);
        
        log.info("Sending hub event with id {} to topic {}", key, hubsTopic);
        kafkaTemplate.send(hubsTopic, key, avroEvent);
    }

    private SensorEventAvro convertToAvro(SensorEvent event) {
        SensorEventAvro avroEvent = new SensorEventAvro();
        avroEvent.setId(event.getId());
        avroEvent.setHubId(event.getHubId());
        
        // Установка временной метки
        if (event.getTimestamp() != null && !event.getTimestamp().isEmpty()) {
            avroEvent.setTimestamp(Instant.parse(event.getTimestamp()));
        } else {
            avroEvent.setTimestamp(Instant.now());
        }

        if (event instanceof ClimateSensorEvent) {
            ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) event;
            ClimateSensorAvro climateSensorAvro = new ClimateSensorAvro();
            climateSensorAvro.setTemperatureC(climateSensorEvent.getTemperatureC());
            climateSensorAvro.setHumidity(climateSensorEvent.getHumidity());
            climateSensorAvro.setCo2Level(climateSensorEvent.getCo2Level());
            avroEvent.setPayload(climateSensorAvro);
        } else if (event instanceof LightSensorEvent) {
            LightSensorEvent lightSensorEvent = (LightSensorEvent) event;
            LightSensorAvro lightSensorAvro = new LightSensorAvro();
            lightSensorAvro.setLinkQuality(lightSensorEvent.getLinkQuality());
            lightSensorAvro.setLuminosity(lightSensorEvent.getLuminosity());
            avroEvent.setPayload(lightSensorAvro);
        } else if (event instanceof MotionSensorEvent) {
            MotionSensorEvent motionSensorEvent = (MotionSensorEvent) event;
            MotionSensorAvro motionSensorAvro = new MotionSensorAvro();
            motionSensorAvro.setLinkQuality(motionSensorEvent.getLinkQuality());
            motionSensorAvro.setMotion(motionSensorEvent.isMotion());
            motionSensorAvro.setVoltage(motionSensorEvent.getVoltage());
            avroEvent.setPayload(motionSensorAvro);
        } else if (event instanceof SwitchSensorEvent) {
            SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) event;
            SwitchSensorAvro switchSensorAvro = new SwitchSensorAvro();
            switchSensorAvro.setState(switchSensorEvent.isState());
            avroEvent.setPayload(switchSensorAvro);
        } else if (event instanceof TemperatureSensorEvent) {
            TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) event;
            TemperatureSensorAvro temperatureSensorAvro = new TemperatureSensorAvro();
            temperatureSensorAvro.setTemperatureC(temperatureSensorEvent.getTemperatureC());
            temperatureSensorAvro.setTemperatureF(temperatureSensorEvent.getTemperatureF());
            avroEvent.setPayload(temperatureSensorAvro);
        }

        return avroEvent;
    }

    private HubEventAvro convertToAvro(HubEvent event) {
        HubEventAvro avroEvent = new HubEventAvro();
        avroEvent.setHubId(event.getHubId());
        
        // Установка временной метки
        if (event.getTimestamp() != null && !event.getTimestamp().isEmpty()) {
            avroEvent.setTimestamp(Instant.parse(event.getTimestamp()));
        } else {
            avroEvent.setTimestamp(Instant.now());
        }

        if (event instanceof DeviceAddedEvent) {
            DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) event;
            DeviceAddedEventAvro deviceAddedEventAvro = new DeviceAddedEventAvro();
            deviceAddedEventAvro.setId(deviceAddedEvent.getId());
            
            DeviceTypeAvro deviceTypeAvro;
            switch (deviceAddedEvent.getDeviceType()) {
                case MOTION_SENSOR:
                    deviceTypeAvro = DeviceTypeAvro.MOTION_SENSOR;
                    break;
                case TEMPERATURE_SENSOR:
                    deviceTypeAvro = DeviceTypeAvro.TEMPERATURE_SENSOR;
                    break;
                case LIGHT_SENSOR:
                    deviceTypeAvro = DeviceTypeAvro.LIGHT_SENSOR;
                    break;
                case CLIMATE_SENSOR:
                    deviceTypeAvro = DeviceTypeAvro.CLIMATE_SENSOR;
                    break;
                case SWITCH_SENSOR:
                    deviceTypeAvro = DeviceTypeAvro.SWITCH_SENSOR;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown device type: " + deviceAddedEvent.getDeviceType());
            }
            
            deviceAddedEventAvro.setType(deviceTypeAvro);
            avroEvent.setPayload(deviceAddedEventAvro);
        } else if (event instanceof DeviceRemovedEvent) {
            DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) event;
            DeviceRemovedEventAvro deviceRemovedEventAvro = new DeviceRemovedEventAvro();
            deviceRemovedEventAvro.setId(deviceRemovedEvent.getId());
            avroEvent.setPayload(deviceRemovedEventAvro);
        } else if (event instanceof ScenarioAddedEvent) {
            ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
            ScenarioAddedEventAvro scenarioAddedEventAvro = new ScenarioAddedEventAvro();
            scenarioAddedEventAvro.setName(scenarioAddedEvent.getName());
            
            scenarioAddedEventAvro.setConditions(java.util.Collections.emptyList());
            scenarioAddedEventAvro.setActions(java.util.Collections.emptyList());
            
            avroEvent.setPayload(scenarioAddedEventAvro);
        } else if (event instanceof ScenarioRemovedEvent) {
            ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
            ScenarioRemovedEventAvro scenarioRemovedEventAvro = new ScenarioRemovedEventAvro();
            scenarioRemovedEventAvro.setName(scenarioRemovedEvent.getName());
            avroEvent.setPayload(scenarioRemovedEventAvro);
        }

        return avroEvent;
    }
} 