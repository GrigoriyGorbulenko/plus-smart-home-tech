package ru.yandex.practicum.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;


@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {

    @PutMapping
    DeliveryDto addNewDelivery(@RequestBody DeliveryDto deliveryDto) throws FeignException;

    @PostMapping("/successful")
    void confirmDelivery(@RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/picked")
    void getProductsToDelivery(@RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/failed")
    void failDelivery(@RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/cost")
    Double calculateTotalDeliveryCost(@RequestBody OrderDto orderDto) throws FeignException;
}
