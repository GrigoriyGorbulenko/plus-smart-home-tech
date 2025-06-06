package ru.yandex.practicum.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.enums.delivery.DeliveryState;

import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDto {
    UUID deliveryId;
    @NotNull
    AddressDto fromAddress;
    @NotNull
    AddressDto toAddress;
    @NotNull
    UUID orderId;
    @NotNull
    DeliveryState state;
}
