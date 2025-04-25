package ru.yandex.practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
public class ScenarioAddedEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    public void addScenario(ScenarioAddedEventAvro eventAvro, String hubId) {
        String name = eventAvro.getName();
        if (scenarioRepository.existsByHubIdAndName(hubId, name)) {
            throw new DuplicateException("Сценарий с названием: " + name + " в хабе с id: "
                    + hubId + " уже есть");
        }
        checkSensorIds(eventAvro, hubId);
        Map<String, Condition> conditions = eventAvro.getConditions().stream()
                .collect(Collectors.toMap(ScenarioConditionAvro::getSensorId, condition -> Condition.builder()
                        .type(mapToConditionType(condition.getType()))
                        .operation(mapToOperationType(condition.getOperation()))
                        .value(extractValue(condition))
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
                .name(name)
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

    private ConditionOperationType mapToOperationType(ConditionOperationAvro typeAvro) {
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

    private Integer extractValue(ScenarioConditionAvro conditionAvro) {
        Object valueObj = conditionAvro.getValue();
        if (valueObj instanceof Integer) {
            return (Integer) valueObj;
        }
        return (Boolean) valueObj ? 1 : 0;
    }

    private void checkSensorIds(ScenarioAddedEventAvro eventAvro, String hubId) {
        Set<String> ids = eventAvro.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId).collect(Collectors.toSet());
        if (ids.size() < eventAvro.getConditions().size()) {
            throw new DuplicateException("Недопустимо указывать одновременно два условия для одного и того же датчика");
        }
        if (sensorRepository.findByIdInAndHubId(ids, hubId).size() != ids.size()) {
            throw new NotFoundException("id некоторых датчиков указанных в условии сценария " +
                    " не найдены в рамках данного хаба");
        }
        ids = eventAvro.getActions().stream().map(DeviceActionAvro::getSensorId).collect(Collectors.toSet());
        if (ids.size() < eventAvro.getActions().size()) {
            throw new DuplicateException("Недопустимо указывать одновременно два действия для одного и того же устройства");
        }
        if (sensorRepository.findByIdInAndHubId(ids, hubId).size() != ids.size()) {
            throw new DuplicateException("id некоторых устройств указанных в действиях по сценарию " +
                    "не найдены в рамках данного хаба");
        }
    }
}
