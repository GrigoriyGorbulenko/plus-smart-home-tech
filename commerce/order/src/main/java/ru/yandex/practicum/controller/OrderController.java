package ru.yandex.practicum.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getOrdersOfUser(@RequestParam String username,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.getOrdersOfUser(username, pageable);
    }

    @PutMapping
    public OrderDto createNewOrder(@RequestBody CreateNewOrderRequest createOrderRequest,
                            @RequestParam String username) {
        return orderService.createNewOrder(createOrderRequest, username);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@RequestBody ProductReturnRequest returnRequest) {
        return orderService.returnOrder(returnRequest);
    }

    @PostMapping("/payment")
    public OrderDto payOrder(@RequestBody UUID orderId) {
        return orderService.payOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto failOrder(@RequestBody UUID orderId) {
        return orderService.failOrder(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto completeDelivery(@RequestBody UUID orderId) {
        return orderService.completeDelivery(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto failDelivery(@RequestBody UUID orderId) {
        return orderService.failDelivery(orderId);
    }

    @PostMapping("/completed")
    public OrderDto completeOrder(@RequestBody UUID orderId) {
        return orderService.completeOrder(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalOrderPrice(@RequestBody UUID orderId) {
        return orderService.calculateTotalOrderPrice(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryPrice(@RequestBody UUID orderId) {
        return orderService.calculateDeliveryPrice(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody UUID orderId) {
        return orderService.assemblyOrder(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto failAssemblyOrder(@RequestBody UUID orderId) {
        return orderService.failAssemblyOrder(orderId);
    }
}
