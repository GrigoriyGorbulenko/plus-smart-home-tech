package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;


@Component
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorProto> {
    public LightSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected LightSensorProto mapToProto(SensorEventProto event) {
        LightSensorProto specialEvent = event.getLightSensorEvent();

        return LightSensorProto.newBuilder()
                .setLinkQuality(specialEvent.getLinkQuality())
                .setLuminosity(specialEvent.getLuminosity())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }
}
