package ru.yandex.practicum.telemetry.collector.service.handler.sensor;


public interface SensorEventHandler {
    SensorEventType getMessageType();

    void handle(SensorEventType event);
}
