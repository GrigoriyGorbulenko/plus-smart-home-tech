package ru.yandex.practicum.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto makingPaymentForOrder(OrderDto orderDto) {
        return paymentService.makingPaymentForOrder(orderDto);
    }

    @PostMapping("/totalCost")
    public Double calculateTotalCost(OrderDto orderDto) {
        return paymentService.calculateTotalCost(orderDto);
    }

    @PostMapping("/refund")
    public void confirmPayment(UUID paymentId) {
        paymentService.confirmPayment(paymentId);
    }

    @PostMapping("/productCost")
    public Double calculateProductsCost(OrderDto orderDto) {
        return paymentService.calculateProductsCost(orderDto);
    }

    @PostMapping("/failed")
    public void failPayment(UUID paymentId) {
        paymentService.failPayment(paymentId);
    }
}
