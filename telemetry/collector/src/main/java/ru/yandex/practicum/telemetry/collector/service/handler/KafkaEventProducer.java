package ru.yandex.practicum.telemetry.collector.service.handler;

import com.google.protobuf.Message;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class KafkaEventProducer implements AutoCloseable {
    protected final Producer<String, Message> producer;

    public void send(Message message, String hubId, Instant timestamp, String topic) {
        ProducerRecord<String, Message> record = new ProducerRecord<>(topic, null,
                timestamp.toEpochMilli(), hubId, message);

        producer.send(record);
        producer.flush();
    }

    @Override
    public void close() {
        producer.flush();
        producer.close(Duration.ofSeconds(10));
    }
}
