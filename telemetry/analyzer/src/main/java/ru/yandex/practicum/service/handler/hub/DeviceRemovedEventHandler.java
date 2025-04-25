package ru.yandex.practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceRemovedEventHandler {
    private final SensorRepository sensorRepository;

    public void deleteDevice(DeviceRemovedEventAvro eventAvro, String hubId) {
        String sensorId = eventAvro.getId();
        if (!sensorRepository.existsByIdAndHubId(sensorId, hubId)) {
            throw new NotFoundException("Устройства с id: " + sensorId + " отсутствует на хабе с id: " + hubId);
        }
        sensorRepository.deleteById(sensorId);
    }
}
