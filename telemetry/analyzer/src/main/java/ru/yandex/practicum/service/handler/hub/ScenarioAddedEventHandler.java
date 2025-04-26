package ru.yandex.practicum.service.handler.hub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.enums.ActionType;
import ru.yandex.practicum.model.enums.ConditionOperationType;
import ru.yandex.practicum.model.enums.ConditionType;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedEventHandler {
    final ScenarioRepository scenarioRepository;
    final ConditionRepository conditionRepository;
    final ActionRepository actionRepository;
    final SensorRepository sensorRepository;

    @Transactional
    public void addScenario(ScenarioAddedEventAvro eventAvro, String hubId) {
        if (scenarioRepository.existsByHubIdAndName(hubId, eventAvro.getName())) {
            throw new DuplicateException("Сценарий с названием: " + eventAvro.getName() + " в хабе с id: "
                    + hubId + " уже есть");
        }
        checkSensorId(eventAvro, hubId);
        Map<String, Condition> conditions = eventAvro.getConditions().stream()
                .collect(Collectors.toMap(ScenarioConditionAvro::getSensorId, condition -> Condition.builder()
                        .type(mapToConditionType(condition.getType()))
                        .operation(mapToConditionOperationType(condition.getOperation()))
                        .value(setValue(condition.getValue()))
                        .build()));
        Map<String, Action> actions = eventAvro.getActions().stream()
                .collect(Collectors.toMap(DeviceActionAvro::getSensorId, action -> Action.builder()
                        .type(mapToActionType(action.getType()))
                        .value(action.getValue())
                        .build()
                ));
        actionRepository.saveAll(actions.values());
        conditionRepository.saveAll(conditions.values());
        scenarioRepository.save(Scenario.builder()
                .hubId(hubId)
                .name(eventAvro.getName())
                .conditions(conditions)
                .actions(actions).build());
    }

    private ConditionType mapToConditionType(ConditionTypeAvro typeAvro) {
        return switch (typeAvro) {
            case MOTION -> ConditionType.MOTION;
            case SWITCH -> ConditionType.SWITCH;
            case CO2LEVEL -> ConditionType.CO2LEVEL;
            case HUMIDITY -> ConditionType.HUMIDITY;
            case LUMINOSITY -> ConditionType.LUMINOSITY;
            case TEMPERATURE -> ConditionType.TEMPERATURE;
        };
    }

    private ConditionOperationType mapToConditionOperationType(ConditionOperationAvro typeAvro) {
        return switch (typeAvro) {
            case EQUALS -> ConditionOperationType.EQUALS;
            case LOWER_THAN -> ConditionOperationType.LOWER_THAN;
            case GREATER_THAN -> ConditionOperationType.GREATER_THAN;
        };
    }

    private ActionType mapToActionType(ActionTypeAvro typeAvro) {
        return switch (typeAvro) {
            case INVERSE -> ActionType.INVERSE;
            case ACTIVATE -> ActionType.ACTIVATE;
            case DEACTIVATE -> ActionType.DEACTIVATE;
            case SET_VALUE -> ActionType.SET_VALUE;
        };
    }

    private Integer setValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return (Boolean) value ? 1 : 0;
    }

    private void checkSensorId(ScenarioAddedEventAvro eventAvro, String hubId) {
        Set<String> sensorConditionId = eventAvro.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId).collect(Collectors.toSet());
        if (sensorConditionId.size() < eventAvro.getConditions().size()) {
            throw new DuplicateException("Несоответствие условиям");
        }
        if (sensorRepository.findByIdInAndHubId(sensorConditionId, hubId).size() != sensorConditionId.size()) {
            throw new NotFoundException("id не найдены");
        }
        Set<String> sensorActionId = eventAvro.getActions().stream().map(DeviceActionAvro::getSensorId).collect(Collectors.toSet());
        if (sensorActionId.size() < eventAvro.getActions().size()) {
            throw new DuplicateException("Несоответствие условиям");
        }
        if (sensorRepository.findByIdInAndHubId(sensorActionId, hubId).size() != sensorActionId.size()) {
            throw new NotFoundException("id не найдены");
        }
    }
}
