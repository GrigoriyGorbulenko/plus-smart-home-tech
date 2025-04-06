package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;

public abstract class BaseHubHandler<T extends SpecificRecordBase> implements HubEventHandler {
}
