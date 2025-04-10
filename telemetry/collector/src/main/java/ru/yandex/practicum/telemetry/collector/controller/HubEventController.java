package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.HubEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.hub.HubEventHandler;


import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Validated
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class HubEventController {
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public HubEventController(List<HubEventHandler> hubEventHandlers) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent request) {
        if (hubEventHandlers.containsKey(request.getType())) {
            hubEventHandlers.get(request.getType()).handle(request);
        } else {
            throw new IllegalArgumentException("Немогу найти обработчик события");
        }
    }
}

