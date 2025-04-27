package ru.yandex.practicum.service.handler.snapshot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SnapshotHandlerImpl implements SnapshotHandler {
    final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();
    final ScenarioRepository scenarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DeviceActionRequest> handle(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.info("Передан снапшот: {}", snapshot);
        if (snapshots.containsKey(hubId)) {
            SensorsSnapshotAvro sensorsSnapshotAvro = snapshots.get(hubId);
            Instant oldSnapshotTimestamp = sensorsSnapshotAvro.getTimestamp();
            Instant newSnapshotTimestamp = snapshot.getTimestamp();
            if (oldSnapshotTimestamp.isAfter(newSnapshotTimestamp)) {
                return List.of();
            }
        }
        snapshots.put(hubId, snapshot);
        List<DeviceActionRequest> actionRequests = new ArrayList<>();
        for (Scenario scenario : scenarioRepository.findByHubId(snapshot.getHubId())) {
            log.info("Получили сценарий с названием: {}", scenario.getName());
            Map<String, Condition> conditions = scenario.getConditions();
            if (!snapshot.getSensorsState().keySet().containsAll(conditions.keySet())) {
                log.debug("Неполные данные");
                continue;
            }
            if (checkConditions(conditions, snapshot)) {
                Map<String, Action> actions = scenario.getActions();
                for (Map.Entry<String, Action> entry : actions.entrySet()) {
                    Action action = entry.getValue();
                    actionRequests.add(DeviceActionRequest.newBuilder()
                            .setScenarioName(scenario.getName())
                            .setHubId(scenario.getHubId())
                            .setAction(DeviceActionProto.newBuilder()
                                    .setSensorId(entry.getKey())
                                    .setType(mapToActionTypeProto(action.getType()))
                                    .setValue(action.getValue())
                                    .build())
                            .build());
                }
            }
        }
        return actionRequests;
    }

    private boolean checkConditions(Map<String, Condition> conditions, SensorsSnapshotAvro snapshotAvro) {
        for (Map.Entry<String, Condition> entry : conditions.entrySet()) {
            Object data = snapshotAvro.getSensorsState().get(entry.getKey()).getData();
            Condition condition = entry.getValue();
            Integer value = condition.getValue();
            ConditionOperationAvro conditionOperationType = condition.getOperation();

            switch (condition.getType()) {
                case TEMPERATURE -> {
                    ClimateSensorAvro climateState = (ClimateSensorAvro) data;
                    checkByConditionOperationType(climateState.getTemperatureC(), value, conditionOperationType);
                }
                case LUMINOSITY -> {
                    LightSensorAvro lightSensorState = (LightSensorAvro) data;
                    checkByConditionOperationType(lightSensorState.getLuminosity(), value, conditionOperationType);
                }
                case HUMIDITY -> {
                    ClimateSensorAvro climateSensorState = (ClimateSensorAvro) data;
                    checkByConditionOperationType(climateSensorState.getHumidity(), value, conditionOperationType);
                }
                case CO2LEVEL -> {
                    ClimateSensorAvro climateSensorState = (ClimateSensorAvro) data;
                    checkByConditionOperationType(climateSensorState.getCo2Level(), value, conditionOperationType);
                }
                case SWITCH -> {
                    SwitchSensorAvro switchSensorState = (SwitchSensorAvro) data;
                    return (switchSensorState.getState() ? 1 : 0) == value;
                }
                case MOTION -> {
                    MotionSensorAvro motionSensorState = (MotionSensorAvro) data;
                    return (motionSensorState.getMotion() ? 1 : 0) == value;
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkByConditionOperationType(int currentValue, int conditionValue, ConditionOperationAvro type) {
        return switch (type) {
            case EQUALS -> currentValue == conditionValue;
            case GREATER_THAN -> currentValue > conditionValue;
            case LOWER_THAN -> currentValue < conditionValue;
        };
    }

    private ActionTypeProto mapToActionTypeProto(ActionTypeAvro actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }
}
