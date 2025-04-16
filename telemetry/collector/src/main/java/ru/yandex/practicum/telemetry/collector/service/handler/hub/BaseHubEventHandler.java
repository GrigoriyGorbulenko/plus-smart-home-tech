package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    private final KafkaEventProducer producer;

    protected abstract T mapToAvro(HubEventProto event);

    @Override
    public void handle(HubEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизвестный тип события: " + event.getPayloadCase());
        }

        T payload = mapToAvro(event);

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(mapTimestampToInstant(event))
                .setPayload(event)
                .build();

        String topic = "telemetry.hubs.v1";
        producer.send(eventAvro,
                event.getHubId(),
                mapTimestampToInstant(event),
                topic);
    }

    private Instant mapTimestampToInstant(HubEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }
}
