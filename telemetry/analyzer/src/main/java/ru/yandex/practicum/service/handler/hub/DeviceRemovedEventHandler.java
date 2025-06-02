package ru.yandex.practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceRemovedEventHandler {
    private final SensorRepository sensorRepository;

    @Transactional
    public void deleteDevice(DeviceRemovedEventAvro eventAvro, String hubId) {
        sensorRepository.findByIdAndHubId(eventAvro.getId(), hubId).orElseThrow(() ->
                new NotFoundException("Устройства с id: " + eventAvro.getId() + " отсутствует на хабе с id: " + hubId));
        sensorRepository.deleteById(eventAvro.getId());
    }
}
