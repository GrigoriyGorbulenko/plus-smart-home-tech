package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AggregatorService {

    Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        if (snapshots.containsKey(event.getHubId())) {
            SensorsSnapshotAvro oldSnapshot = snapshots.get(event.getHubId());
            Optional<SensorsSnapshotAvro> optSensorsSnapshotAvro = updateSnapshot(oldSnapshot, event);
            optSensorsSnapshotAvro.ifPresent(sensorsSnapshotAvro -> snapshots.put(event.getHubId(), sensorsSnapshotAvro));
            return optSensorsSnapshotAvro;
        } else {
            SensorsSnapshotAvro snapshot = createSnapshot(event);
            snapshots.put(event.getHubId(), snapshot);
            return Optional.of(snapshot);
        }
    }

    private SensorsSnapshotAvro createSnapshot(SensorEventAvro event) {
        Map<String, SensorStateAvro> sensorStates = new HashMap<>();
        SensorStateAvro sensorState = createSensorState(event);
        sensorStates.put(event.getId(), sensorState);

        return SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.now())
                .setSensorsState(sensorStates)
                .build();
    }

    private Optional<SensorsSnapshotAvro> updateSnapshot(SensorsSnapshotAvro oldSnapshot, SensorEventAvro event) {

        if (oldSnapshot.getSensorsState().containsKey(event.getId())) {
            if (oldSnapshot.getSensorsState().get(event.getId()).getTimestamp().isAfter(event.getTimestamp()) ||
                    oldSnapshot.getSensorsState().get(event.getId()).getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }
        SensorStateAvro sensorState = createSensorState(event);

        oldSnapshot.getSensorsState().put(event.getId(), sensorState);
        oldSnapshot.setTimestamp(event.getTimestamp());

        return Optional.of(oldSnapshot);
    }

    private SensorStateAvro createSensorState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }
}
