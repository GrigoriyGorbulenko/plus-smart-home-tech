package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseHubEventHandler implements HubEventHandler {
    private final KafkaEventProducer producer;

    protected abstract HubEventAvro mapToAvro(HubEventProto event);

    @Override
    public void handle(HubEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизвестный тип события: " + event.getPayloadCase());
        }

        HubEventAvro hubEventAvro = mapToAvro(event);

        String topic = "telemetry.hubs.v1";
        producer.send(hubEventAvro,
                event.getHubId(),
                mapTimestampToInstant(event),
                topic);
    }

    Instant mapTimestampToInstant(HubEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }
}
