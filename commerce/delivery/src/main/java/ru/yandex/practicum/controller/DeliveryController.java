package ru.yandex.practicum.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto addNewDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        return deliveryService.addNewDelivery(deliveryDto);
    }

    @PostMapping("/successful")
    public void confirmDelivery(@RequestBody UUID deliveryId) {
        deliveryService.confirmDelivery(deliveryId);
    }

    @PostMapping("/picked")
    public void getProductsToDelivery(@RequestBody UUID deliveryId) {
        deliveryService.getProductsToDelivery(deliveryId);
    }

    @PostMapping("/failed")
    public void failDelivery(@RequestBody UUID deliveryId) {
        deliveryService.failDelivery(deliveryId);
    }

    @PostMapping("/cost")
    public Double calculateTotalDeliveryCost(@RequestBody OrderDto orderDto) {
        return deliveryService.calculateTotalDeliveryCost(orderDto);
    }
}
