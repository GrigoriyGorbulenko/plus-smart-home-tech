package ru.yandex.practicum.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient {

    @PostMapping
    PaymentDto makingPaymentForOrder(@RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/totalCost")
    Double calculateTotalCost(@RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/refund")
    void confirmPayment(@RequestBody UUID paymentId) throws FeignException;

    @PostMapping("/productCost")
    Double calculateProductsCost(@RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/failed")
    void failPayment(@RequestBody UUID paymentId) throws FeignException;
}
