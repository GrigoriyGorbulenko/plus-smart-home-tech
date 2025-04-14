package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorProto> {
    public TemperatureSensorEventHandler(KafkaEventProducer producer) {super(producer); }

    @Override
    protected TemperatureSensorProto mapToProto(SensorEventProto event) {
        TemperatureSensorProto specialEvent = event.getTemperatureSensorEvent();

        return TemperatureSensorProto.newBuilder()
                .setTemperatureC(specialEvent.getTemperatureC())
                .setTemperatureF(specialEvent.getTemperatureF())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }
}
