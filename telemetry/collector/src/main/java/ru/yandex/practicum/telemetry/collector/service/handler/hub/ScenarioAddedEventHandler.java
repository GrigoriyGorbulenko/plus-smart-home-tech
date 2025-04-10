package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.hub.DeviceAction;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.ScenarioCondition;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.HubEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

import java.util.List;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {

        ScenarioAddedEvent _event = (ScenarioAddedEvent) event;
        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setConditions(mapToConditionTypeAvro(_event.getConditions()))
                .setActions(mapToDeviceActionAvro(_event.getActions()))
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    private List<ScenarioConditionAvro> mapToConditionTypeAvro(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(condition.getSensorId())
                        .setType(
                                switch (condition.getType()) {
                                    case MOTION -> ConditionTypeAvro.MOTION;
                                    case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
                                    case SWITCH -> ConditionTypeAvro.SWITCH;
                                    case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
                                    case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
                                    case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
                                })
                        .setOperation(
                                switch (condition.getOperation()) {
                                    case EQUALS -> ConditionOperationAvro.EQUALS;
                                    case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
                                    case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
                                }
                        )
                        .setValue(condition.getValue())
                        .build())
                .toList();
    }

    private List<DeviceActionAvro> mapToDeviceActionAvro(List<DeviceAction> deviceActions) {
        return deviceActions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(
                                switch (action.getType()) {
                                    case ACTIVATE -> ActionTypeAvro.ACTIVATE;
                                    case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
                                    case INVERSE -> ActionTypeAvro.INVERSE;
                                    case SET_VALUE -> ActionTypeAvro.SET_VALUE;
                                }
                        )
                        .setValue(action.getValue())
                        .build())
                .toList();
    }
}
