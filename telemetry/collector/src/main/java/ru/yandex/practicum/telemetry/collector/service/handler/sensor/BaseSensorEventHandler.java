package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;

public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
}
