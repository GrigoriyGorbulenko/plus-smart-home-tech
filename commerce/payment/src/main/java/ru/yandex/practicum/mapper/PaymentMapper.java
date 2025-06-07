package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.enums.payment.PaymentState;
import ru.yandex.practicum.model.Payment;

@UtilityClass
public class PaymentMapper {

    public Payment mapToPayment(OrderDto dto) {
        return Payment.builder()
                .orderId(dto.getOrderId())
                .state(PaymentState.PENDING)
                .totalPayment(dto.getTotalPrice())
                .deliveryTotal(dto.getDeliveryPrice())
                .productsTotal(dto.getProductPrice())
                .build();
    }

    public PaymentDto mapToPaymentDto(Payment payment, Double tax) {
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .totalPayment(payment.getTotalPayment())
                .deliveryTotal(payment.getDeliveryTotal())
                .feeTotal(payment.getProductsTotal() * tax)
                .build();
    }
}
