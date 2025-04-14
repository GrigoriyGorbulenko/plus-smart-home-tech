package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.collector.model.sensor.Sensor;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.SwitchSensor;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorProto> {
    public SwitchSensorEventHandler(KafkaEventProducer producer) {super(producer); }

    @Override
    protected SwitchSensorProto mapToProto(SensorEventProto event) {
        SwitchSensorProto specialEvent = event.getSwitchSensorEvent();

        return SwitchSensorProto.newBuilder()
                .setState(specialEvent.getState())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }
}
