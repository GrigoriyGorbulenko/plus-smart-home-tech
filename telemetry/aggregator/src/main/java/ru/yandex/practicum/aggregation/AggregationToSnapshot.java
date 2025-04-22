package ru.yandex.practicum.aggregation;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;

public class AggregationToSnapshot {

    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
//        Проверяем, есть ли снапшот для event.getHubId()
//        Если снапшот есть, то достаём его
//        Если нет, то созадём новый
//
//        Проверяем, есть ли в снапшоте данные для event.getId()
//        Если данные есть, то достаём их в переменную oldState
//        Проверка, если oldState.getTimestamp() произошёл позже, чем
//        event.getTimestamp() или oldState.getData() равен
//        event.getPayload(), то ничего обнавлять не нужно, выходим из метода
//        вернув Optional.empty()
//
//        // если дошли до сюда, значит, пришли новые данные и
//        // снапшот нужно обновить
//        Создаём экземпляр SensorStateAvro на основе данных события
//        Добавляем полученный экземпляр в снапшот
//        Обновляем таймстемп снапшота таймстемпом из события
//        Возвращаем снапшот - Optional.of(snapshot)
    }
}
