package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEventProto event) {
        DeviceAddedEventProto specialEvent = event.getDeviceAdded();

        return DeviceAddedEventAvro.newBuilder()
                .setId(specialEvent.getId())
                .setType(mapToDeviceTypeAvro(specialEvent.getType()))
                .build();

    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    private DeviceTypeAvro mapToDeviceTypeAvro(DeviceTypeProto deviceTypeProto) {
        return switch (deviceTypeProto) {
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            default -> throw new IllegalStateException("Unexpected value: " + deviceTypeProto);
        };
    }
}
