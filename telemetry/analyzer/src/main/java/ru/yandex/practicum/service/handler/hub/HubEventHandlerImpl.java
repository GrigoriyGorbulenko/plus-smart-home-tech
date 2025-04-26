package ru.yandex.practicum.service.handler.hub;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HubEventHandlerImpl implements HubEventHandler {
    final DeviceAddedEventHandler deviceAddedEventHandler;
    final DeviceRemovedEventHandler deviceRemovedEventHandler;
    final ScenarioAddedEventHandler scenarioAddedEventHandler;
    final ScenarioRemovedEventHandler scenarioRemovedEventHandler;


    @Override
    public void handle(HubEventAvro hubEvent) {
        Object payload = hubEvent.getPayload();
        String hubId = hubEvent.getHubId();
        switch (payload) {
            case DeviceAddedEventAvro eventAvro -> deviceAddedEventHandler.addDevice(eventAvro, hubId);
            case DeviceRemovedEventAvro eventAvro -> deviceRemovedEventHandler.deleteDevice(eventAvro, hubId);
            case ScenarioAddedEventAvro eventAvro -> scenarioAddedEventHandler.addScenario(eventAvro, hubId);
            case ScenarioRemovedEventAvro eventAvro -> scenarioRemovedEventHandler.deleteScenario(eventAvro, hubId);
            default -> throw new IllegalStateException("Unexpected value: " + payload);
        }
    }
}
