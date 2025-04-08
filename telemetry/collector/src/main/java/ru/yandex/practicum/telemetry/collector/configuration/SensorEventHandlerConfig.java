package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.telemetry.collector.model.sensor.*;
import ru.yandex.practicum.telemetry.collector.service.handler.sensor.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SensorEventHandlerConfig {
    private final ClimateSensorEventHandler climateHandler;
    private final LightSensorEventHandler lightHandler;
    private final MotionSensorEventHandler motionHandler;
    private final SwitchSensorEventHandler switchHandler;
    private final TemperatureSensorEventHandler temperatureHandler;

    @Bean
    public Map<SensorEventType, SensorEventHandler> getSensorEventHandlers() {
        Map<SensorEventType, SensorEventHandler> sensorEventHandlers = new HashMap<>();

        sensorEventHandlers.put(SensorEventType.SWITCH_SENSOR_EVENT, switchHandler);
        sensorEventHandlers.put(SensorEventType.CLIMATE_SENSOR_EVENT, climateHandler);
        sensorEventHandlers.put(SensorEventType.LIGHT_SENSOR_EVENT, lightHandler);
        sensorEventHandlers.put(SensorEventType.MOTION_SENSOR_EVENT, motionHandler);
        sensorEventHandlers.put(SensorEventType.TEMPERATURE_SENSOR_EVENT, temperatureHandler);

        return sensorEventHandlers;
    }
}
