package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.enums.delivery.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.exception.OrderBookingNotFoundException;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.AddressRepository;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    private static final Double BASE_DELIVERY_PRICE = 5.0;
    private static final String ADDRESS1 = "ADDRESS_1";
    private static final String ADDRESS2 = "ADDRESS_2";
    private static final Double FRAGILE_RATIO = 0.3;
    private static final Double WEIGHT_RATIO = 0.3;
    private static final Double VOLUME_RATIO = 0.2;
    private static final Double ADDRESS1_RATIO = 1.0;
    private static final Double ADDRESS2_RATIO = 2.0;
    private static final Double NOT_SAME_ADDRESS_RATIO = 1.2;


    @Override
    @Transactional
    public DeliveryDto addNewDelivery(DeliveryDto deliveryDto) {
        Address fromAddress = addressRepository.save(AddressMapper.mapToAddress(deliveryDto.getFromAddress()));
        Address toAddress = addressRepository.save(AddressMapper.mapToAddress(deliveryDto.getToAddress()));

        return DeliveryMapper.mapToDeliveryDto(deliveryRepository.save(DeliveryMapper.mapToDelivery(deliveryDto, fromAddress,
                toAddress)));
    }

    @Override
    @Transactional
    public void confirmDelivery(UUID deliveryId) {
        Delivery delivery = checkDelivery(deliveryId);
        delivery.setState(DeliveryState.DELIVERED);
        orderClient.completeDelivery(delivery.getOrderId());
    }

    @Override
    @Transactional
    public void getProductsToDelivery(UUID deliveryId) {
        Delivery delivery = checkDelivery(deliveryId);

        try {
            log.info("Отправляем на склад для передачи товаров в доставку");
            warehouseClient.shippedToDelivery(ShippedToDeliveryRequest.builder()
                    .deliveryId(deliveryId)
                    .orderId(delivery.getOrderId())
                    .build());
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new OrderBookingNotFoundException(e.getMessage());
            } else {
                throw e;
            }
        }
        delivery.setState(DeliveryState.IN_PROGRESS);
    }

    @Override
    public void failDelivery(UUID deliveryId) {
        Delivery delivery = checkDelivery(deliveryId);
        delivery.setState(DeliveryState.FAILED);
        orderClient.failDelivery(delivery.getOrderId());
    }

    @Override
    @Transactional
    public Double calculateTotalDeliveryCost(OrderDto orderDto) {
        Delivery delivery = checkDelivery(orderDto.getOrderId());
        double cost = BASE_DELIVERY_PRICE;
        cost += BASE_DELIVERY_PRICE * getRatioByFromAddress(delivery.getFromAddress());
        cost *= getRatioByFragile(orderDto.getFragile());
        cost += orderDto.getDeliveryWeight() * WEIGHT_RATIO;
        cost += orderDto.getDeliveryVolume() * VOLUME_RATIO;
        cost *= getRatioByToAddress(delivery.getFromAddress(), delivery.getToAddress());
        return cost;
    }

    private Double getRatioByFromAddress(Address address) {
        String addressStr = address.toString();
        if (addressStr.contains(ADDRESS1)) {
            return ADDRESS1_RATIO;
        } else if (addressStr.contains(ADDRESS2)) {
            return ADDRESS2_RATIO;
        } else {
            return BASE_DELIVERY_PRICE;
        }
    }

    private Double getRatioByToAddress(Address from, Address to) {
        if (!from.getStreet().equals(to.getStreet())) {
            return NOT_SAME_ADDRESS_RATIO;
        }
        return 1.0;
    }

    private Double getRatioByFragile(boolean isFragile) {
        if (isFragile) {
            return FRAGILE_RATIO;
        }
        return 1.0;
    }

    private Delivery checkDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена"));
    }
}
