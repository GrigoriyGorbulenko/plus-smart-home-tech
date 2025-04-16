package ru.yandex.practicum.telemetry.collector.model.sensor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = SensorEventType.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MotionSensor.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensor.class, name = "TEMPERATURE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensor.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = ClimateSensor.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensor.class, name = "SWITCH_SENSOR_EVENT")
})

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Sensor {
    @NotBlank
    String id;
    @NotBlank
    String hubId;
    Instant timestamp = Instant.now();

    @NotNull
    public abstract SensorEventType getType();
}
