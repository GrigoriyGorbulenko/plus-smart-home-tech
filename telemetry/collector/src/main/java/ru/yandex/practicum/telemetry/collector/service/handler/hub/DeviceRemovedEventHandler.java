package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.HubEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

@Component
public class DeviceRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {
    public DeviceRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected DeviceRemovedEventAvro mapToAvro(HubEvent event) {
        DeviceRemovedEvent specialEvent = (DeviceRemovedEvent) event;

        return DeviceRemovedEventAvro.newBuilder()
                .setId(specialEvent.getId())
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
