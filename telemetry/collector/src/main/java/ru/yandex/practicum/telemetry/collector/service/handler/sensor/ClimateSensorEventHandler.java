package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.handler.KafkaEventProducer;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler<ClimateSensorProto> {
    public ClimateSensorEventHandler(KafkaEventProducer producer) {super(producer); }

    @Override
    protected ClimateSensorProto mapToProto(SensorEventProto event) {
        ClimateSensorProto specialEvent = event.getClimateSensorEvent();

        return ClimateSensorProto.newBuilder()
                .setTemperatureC(specialEvent.getTemperatureC())
                .setHumidity(specialEvent.getHumidity())
                .setCo2Level(specialEvent.getCo2Level())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }
}
