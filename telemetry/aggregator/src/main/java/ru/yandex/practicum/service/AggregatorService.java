package ru.yandex.practicum.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AggregatorService {

    Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(ConsumerRecord<String, SensorEventAvro> record) {

        SensorsSnapshotAvro snapshot;
        if (snapshots.containsKey(record.key())) {
            snapshot = snapshots.get(record.key());
            Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
            SensorEventAvro event = record.value();

            if (sensorsState.containsKey(event.getId())) {
                SensorStateAvro oldSensorState = sensorsState.get(event.getId());
                if (oldSensorState.getTimestamp().isAfter(event.getTimestamp()) ||
                        oldSensorState.getData().equals(event.getPayload())) {
                    return Optional.empty();
                }
            }
            sensorsState.put(event.getId(), createSensorState(event));
            snapshot.setTimestamp(event.getTimestamp());

        } else {
            SensorEventAvro event = record.value();
            Map<String, SensorStateAvro> sensorsState = new HashMap<>();

            sensorsState.put(event.getId(), createSensorState(event));

            snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(record.key())
                    .setTimestamp(event.getTimestamp())
                    .setSensorsState(sensorsState)
                    .build();

            snapshots.put(record.key(), snapshot);
        }
        return Optional.of(snapshot);
    }

    private SensorStateAvro createSensorState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }

}
