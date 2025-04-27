package ru.yandex.practicum.service.processor.snapshot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import ru.yandex.practicum.service.handler.snapshot.SnapshotHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SnapshotProcessor implements Runnable {
    final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;
    final Consumer<String, SensorsSnapshotAvro> consumer;
    final SnapshotHandler snapshotHandler;
    static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    @Value("${topic.snapshots-topic}")
    String snapshotTopic;

    public SnapshotProcessor(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient,
                             Consumer<String, SensorsSnapshotAvro> consumer,
                             SnapshotHandler snapshotHandler) {
        this.hubRouterClient = hubRouterClient;
        this.consumer = consumer;
        this.snapshotHandler = snapshotHandler;
    }


    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(snapshotTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                int count = 0;
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    for (DeviceActionRequest action : snapshotHandler.handle(record.value())) {
                        try {
                            hubRouterClient.handleDeviceAction(action);
                            log.info("Отправлен запрос действия: {}", action);
                        } catch (DuplicateException | NotFoundException e) {
                            log.error("Ошибка во время обработки событий от датчиков", e);
                        }
                    }

                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, SensorsSnapshotAvro> record, int count, Consumer<String, SensorsSnapshotAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}

