package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

import java.util.List;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler {
    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected HubEventAvro mapToAvro(HubEventProto event) {

        ScenarioAddedEventProto specialEvent = event.getScenarioAdded();
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(mapTimestampToInstant(event))
                .setPayload(new ScenarioAddedEventAvro(specialEvent.getName(),
                        mapToConditionTypeAvro(specialEvent.getConditionList()),
                        mapToDeviceActionAvro(specialEvent.getActionList())))
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    private List<ScenarioConditionAvro> mapToConditionTypeAvro(List<ScenarioConditionProto> conditions) {
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
                                    default ->
                                            throw new IllegalStateException("Unexpected value: " + condition.getType());
                                })
                        .setOperation(
                                switch (condition.getOperation()) {
                                    case EQUALS -> ConditionOperationAvro.EQUALS;
                                    case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
                                    case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
                                    default ->
                                            throw new IllegalStateException("Unexpected value: " + condition.getOperation());
                                }
                        )
                        .setValue(condition.getValueCase())
                        .build())
                .toList();
    }

    private List<DeviceActionAvro> mapToDeviceActionAvro(List<DeviceActionProto> deviceActions) {
        return deviceActions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(
                                switch (action.getType()) {
                                    case ACTIVATE -> ActionTypeAvro.ACTIVATE;
                                    case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
                                    case INVERSE -> ActionTypeAvro.INVERSE;
                                    case SET_VALUE -> ActionTypeAvro.SET_VALUE;
                                    default -> throw new IllegalStateException("Unexpected value: " + action.getType());
                                }
                        )
                        .setValue(action.getValue())
                        .build())
                .toList();
    }
}
