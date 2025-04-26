package ru.yandex.practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Transactional
    public void deleteScenario(ScenarioRemovedEventAvro eventAvro, String hubId) {
        Scenario optScenario = scenarioRepository.findByHubIdAndName(hubId, eventAvro.getName())
                .orElseThrow(() -> new NotFoundException("Сценарий c названием: " + eventAvro.getName() +
                        " не найден в хабе c id: " + hubId));
        Set<Long> conditionIds = optScenario.getConditions().values().stream().map(Condition::getId).collect(Collectors.toSet());
        conditionRepository.deleteAllById(conditionIds);
        Set<Long> actionIds = optScenario.getActions().values().stream().map(Action::getId).collect(Collectors.toSet());
        actionRepository.deleteAllById(actionIds);
        scenarioRepository.deleteById(optScenario.getId());
    }
}
