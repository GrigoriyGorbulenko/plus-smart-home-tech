package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.enums.order.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.exception.ProductNotFoundInWarehouseException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.feign.DeliveryClient;
import ru.yandex.practicum.feign.PaymentClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    public List<OrderDto> getOrdersOfUser(String username, Pageable pageable) {
        return orderRepository.findAllByUserName(username, pageable).stream().map(OrderMapper::mapToOrderDto).toList();
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest createOrderRequest, String username) {
        Order order = Order.builder()
                .shoppingCartId(createOrderRequest.getShoppingCart().getShoppingCartId())
                .products(createOrderRequest.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .build();
        Order newOrder = orderRepository.save(order);

        BookedProductsDto bookedProducts = warehouseClient.assemblyProductsForOrder(
                AssemblyProductsForOrderRequest.builder()
                        .orderId(newOrder.getOrderId())
                        .products(createOrderRequest.getShoppingCart().getProducts())
                        .build());

        newOrder.setFragile(bookedProducts.getFragile());
        newOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        newOrder.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        newOrder.setProductPrice(paymentClient.calculateProductsCost(OrderMapper.mapToOrderDto(newOrder)));

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(newOrder.getOrderId())
                .fromAddress(warehouseClient.getWarehouseAddress())
                .toAddress(createOrderRequest.getDeliveryAddress())
                .build();
        newOrder.setDeliveryId(deliveryClient.addNewDelivery(deliveryDto).getDeliveryId());

        paymentClient.makingPaymentForOrder(OrderMapper.mapToOrderDto(newOrder));
        return OrderMapper.mapToOrderDto(newOrder);
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest returnRequest) {
        Order order = checkOrder(returnRequest.getOrderId());
        warehouseClient.returnProducts(returnRequest.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto payOrder(UUID orderId) {
        Order order = checkOrder(orderId);
        order.setState(OrderState.PAID);
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto failOrder(UUID orderId) {
        Order order = checkOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto completeDelivery(UUID orderId) {
        Order order = checkOrder(orderId);
        order.setState(OrderState.DELIVERED);
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto failDelivery(UUID orderId) {
        Order order = checkOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        Order order = checkOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateTotalOrderPrice(UUID orderId) {
        Order order = checkOrder(orderId);
        order.setTotalPrice(paymentClient.calculateTotalCost(OrderMapper.mapToOrderDto(order)));
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryPrice(UUID orderId) {
        Order order = checkOrder(orderId);
        order.setDeliveryPrice(deliveryClient.calculateTotalDeliveryCost(OrderMapper.mapToOrderDto(order)));
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto assemblyOrder(UUID orderId) {
        Order order = checkOrder(orderId);

        if (order.getState() != OrderState.NEW) {
            throw new ValidationException("Не новый заказ");
        }

        try {
            log.info("Отправляем на сборку заказа на складе");
            warehouseClient.assemblyProductsForOrder(AssemblyProductsForOrderRequest.builder()
                    .orderId(order.getOrderId())
                    .products(order.getProducts())
                    .build());
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ProductNotFoundInWarehouseException(e.getMessage());
            } else if (e.status() == 400) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(e.getMessage());
            } else {
                throw e;
            }
        }

        order.setState(OrderState.ASSEMBLED);

        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto failAssemblyOrder(UUID orderId) {
        Order Order = checkOrder(orderId);

        Order.setState(OrderState.ASSEMBLY_FAILED);

        return OrderMapper.mapToOrderDto(Order);
    }

    private Order checkOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
    }
}
