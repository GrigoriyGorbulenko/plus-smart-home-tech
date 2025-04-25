package ru.yandex.practicum.service.handler.snapshot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SnapshotHandlerImpl {
    final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();
    final ScenarioRepository scenarioRepository;
}
