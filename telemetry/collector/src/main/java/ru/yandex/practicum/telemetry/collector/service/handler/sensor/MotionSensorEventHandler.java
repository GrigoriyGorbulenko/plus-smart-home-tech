package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.telemetry.collector.model.sensor.MotionSensor;
import ru.yandex.practicum.telemetry.collector.model.sensor.Sensor;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorProto> {
    public MotionSensorEventHandler(KafkaEventProducer producer) {super(producer); }

    @Override
    protected MotionSensorProto mapToProto(SensorEventProto event) {
        MotionSensorProto specialEvent = event.getMotionSensorEvent();

        return MotionSensorProto.newBuilder()
                .setLinkQuality(specialEvent.getLinkQuality())
                .setMotion(specialEvent.getMotion())
                .setVoltage(specialEvent.getVoltage())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }
}
