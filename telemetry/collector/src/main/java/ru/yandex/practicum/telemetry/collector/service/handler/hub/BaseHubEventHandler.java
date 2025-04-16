package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import com.google.protobuf.Message;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends Message> implements HubEventHandler {
    private final KafkaEventProducer producer;

    protected abstract T mapToProto(HubEventProto event);

    @Override
    public void handle(HubEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизвестный тип события: " + event.getPayloadCase());
        }

        T payload = mapToProto(event);

        HubEventProto eventProto = HubEventProto.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .se
                .build();

        String topic = "telemetry.hubs.v1";
        producer.send(eventProto,
                event.getHubId(),
                mapTimestampToInstant(event),
                topic);
    }

    private Instant mapTimestampToInstant(HubEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }
}
