package ru.yandex.practicum.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {

    @GetMapping
    List<OrderDto> getOrdersOfUser(@RequestParam String username,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) throws FeignException;

    @PutMapping
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest createOrderRequest,
                            @RequestParam String username) throws FeignException;

    @PostMapping("/return")
    OrderDto returnOrder(@RequestBody ProductReturnRequest returnRequest) throws FeignException;

    @PostMapping("/payment")
    OrderDto payOrder(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/payment/failed")
    OrderDto failOrder(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery")
    OrderDto completeDelivery(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/failed")
    OrderDto failDelivery(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/completed")
    OrderDto completeOrder(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/total")
    OrderDto calculateTotalOrderPrice(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryPrice(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly/failed")
    OrderDto failAssemblyOrder(@RequestBody UUID orderId) throws FeignException;
}
