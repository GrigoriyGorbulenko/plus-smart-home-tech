package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {

    DeliveryDto addNewDelivery(DeliveryDto deliveryDto);

    void confirmDelivery(UUID deliveryId);

    void getProductsToDelivery( UUID deliveryId);

    void failDelivery(UUID deliveryId);

    Double calculateTotalDeliveryCost(OrderDto orderDto);
}
