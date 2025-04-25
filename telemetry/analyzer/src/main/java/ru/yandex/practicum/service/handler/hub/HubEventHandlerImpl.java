package ru.yandex.practicum.service.handler.hub;

import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.*;

public class HubEventHandlerImpl implements HubEventHandler {
    DeviceAddedEventHandler deviceAddedEventHandler;
    DeviceRemovedEventHandler deviceRemovedEventHandler;
    ScenarioAddedEventHandler scenarioAddedEventHandler;
    ScenarioRemovedEventHandler scenarioRemovedEventHandler;

    @Override
    public void handle(HubEventAvro hubEvent) {
        Object payload = hubEvent.getPayload();
        String hubId = hubEvent.getHubId();
        switch (payload) {
            case DeviceAddedEventAvro eventAvro -> deviceAddedEventHandler.addDevice(eventAvro, hubId);
            case DeviceRemovedEventAvro eventAvro -> deviceRemovedEventHandler.deleteDevice(eventAvro, hubId);
            case ScenarioAddedEventAvro eventAvro -> scenarioAddedEventHandler.addScenario(eventAvro, hubId);
            case ScenarioRemovedEventAvro eventAvro -> scenarioRemovedEventHandler.deleteScenario(eventAvro, hubId);
            default -> throw new NotFoundException("Обработчик не найден");
        }
    }
}
