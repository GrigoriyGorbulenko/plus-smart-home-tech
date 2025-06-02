package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

import java.time.Instant;


@RequiredArgsConstructor
public abstract class BaseSensorEventHandler implements SensorEventHandler {
    private final KafkaEventProducer producer;

    protected abstract SensorEventAvro mapToAvro(SensorEventProto event);

    @Override
    public void handle(SensorEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизвестный тип события: " + event.getPayloadCase());
        }

        SensorEventAvro sensorEventAvro = mapToAvro(event);

        String topic = "telemetry.sensors.v1";
        producer.send(sensorEventAvro,
                event.getHubId(),
                mapTimestampToInstant(event),
                topic);
    }

    Instant mapTimestampToInstant(SensorEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }
}
