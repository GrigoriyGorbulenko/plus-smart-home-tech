package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderDto> getOrdersOfUser(String username, Pageable pageable);

    OrderDto createNewOrder(CreateNewOrderRequest createOrderRequest, String username);

    OrderDto returnOrder(ProductReturnRequest returnRequest);

    OrderDto payOrder(UUID orderId);

    OrderDto failOrder(UUID orderId);

    OrderDto completeDelivery(UUID orderId);

    OrderDto failDelivery(UUID orderId);

    OrderDto completeOrder(UUID orderId);

    OrderDto calculateTotalOrderPrice( UUID orderId);

    OrderDto calculateDeliveryPrice(UUID orderId);

    OrderDto assemblyOrder(UUID orderId);

    OrderDto failAssemblyOrder(UUID orderId);
}
