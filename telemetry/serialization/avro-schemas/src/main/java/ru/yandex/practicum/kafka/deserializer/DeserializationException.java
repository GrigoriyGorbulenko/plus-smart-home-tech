package ru.yandex.practicum.kafka.deserializer;

import org.apache.kafka.common.KafkaException;

public class DeserializationException extends KafkaException {
    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
