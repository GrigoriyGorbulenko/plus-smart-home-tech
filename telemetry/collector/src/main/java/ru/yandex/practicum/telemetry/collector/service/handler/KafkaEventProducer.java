package ru.yandex.practicum.telemetry.collector.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;


@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventProducer implements AutoCloseable {

    protected final KafkaProducer<String, SpecificRecordBase> producer;

    public void send(SpecificRecordBase message, String hubId, Instant timestamp, String topic) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null,
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
