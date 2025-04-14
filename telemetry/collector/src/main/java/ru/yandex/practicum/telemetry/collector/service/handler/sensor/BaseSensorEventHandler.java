package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import com.google.protobuf.Message;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

import java.time.Instant;


@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends Message> implements SensorEventHandler {
    private final KafkaEventProducer producer;

    protected abstract T mapToProto(SensorEventProto event);

    @Override
    public void handle(SensorEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизвестный тип события: " + event.getPayloadCase());
        }

        T payload = mapToProto(event);

        SensorEventProto eventProto = SensorEventProto.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(event.getTimestamp())
                .build();

        String topic = "telemetry.sensors.v1";
        producer.send(eventProto,
                event.getHubId(),
                mapTimestampToInstant(event),
                topic);
    }

    private Instant mapTimestampToInstant(SensorEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }
}
