package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentService {

    PaymentDto makingPaymentForOrder(OrderDto orderDto);

    Double calculateTotalCost(OrderDto orderDto);

    void confirmPayment(UUID paymentId);

    Double calculateProductsCost(OrderDto orderDto);

    void failPayment(UUID paymentId);
}
