package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.enums.payment.PaymentState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;
    private static final Double TAX = 0.1;

    @Override
    @Transactional
    public PaymentDto makingPaymentForOrder(OrderDto orderDto) {
        if (orderDto.getTotalPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Для оплаты не хватает информации в заказе");
        }
        return PaymentMapper.mapToPaymentDto(paymentRepository.save(PaymentMapper.mapToPayment(orderDto)), TAX);
    }

    @Override
    public Double calculateTotalCost(OrderDto orderDto) {
        Double productsPrice = checkProductAndDeliveryPrice(orderDto);
        log.info("Считаем полную стоимость заказа");
        return productsPrice + productsPrice * TAX + orderDto.getDeliveryPrice();
    }

    @Override
    @Transactional
    public void confirmPayment(UUID paymentId) {
        Payment payment = checkPayment(paymentId);
        payment.setState(PaymentState.SUCCESS);
        failedOrder(payment);
    }

    @Override
    public Double calculateProductsCost(OrderDto orderDto) {
        try {
            Map<UUID, Long> products = orderDto.getProducts();

            Map<UUID, Float> productsPrice = products.keySet().stream()
                    .map(shoppingStoreClient::getProductById)
                    .collect(Collectors.toMap(ProductDto::getProductId, ProductDto::getPrice));

            return products.entrySet().stream()
                    .map(entry -> entry.getValue() * productsPrice.get(entry.getKey()))
                    .mapToDouble(Float::floatValue)
                    .sum();
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ProductNotFoundException("Продукт не найден");
            } else {
                throw e;
            }
        }
    }

    @Override
    @Transactional
    public void failPayment(UUID paymentId) {
        Payment payment = checkPayment(paymentId);
        payment.setState(PaymentState.FAILED);
        failedOrder(payment);

    }

    private Payment checkPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ProductNotFoundException("Оплата не найдена"));
    }

    private void failedOrder(Payment payment) {
        try {
            orderClient.failOrder(payment.getOrderId());
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new NoOrderFoundException(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    private Double checkProductAndDeliveryPrice(OrderDto orderDto) {
        Double price = orderDto.getProductPrice();
        if (price == null || orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не указана цена");
        }
        return price;
    }
}
