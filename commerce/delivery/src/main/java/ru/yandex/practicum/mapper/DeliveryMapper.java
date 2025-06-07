package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;

public class DeliveryMapper {
    public Delivery mapToDelivery(DeliveryDto deliveryDto, Address fromAddress, Address toAddress) {
        return Delivery.builder()
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .orderId(deliveryDto.getOrderId())
                .state(deliveryDto.getState())
                .build();
    }

    public DeliveryDto mapToDeliveryDto(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .fromAddress(AddressMapper.mapToAddressDto(delivery.getFromAddress()))
                .toAddress(AddressMapper.mapToAddressDto(delivery.getToAddress()))
                .orderId(delivery.getOrderId())
                .state(delivery.getState())
                .build();
    }
}
