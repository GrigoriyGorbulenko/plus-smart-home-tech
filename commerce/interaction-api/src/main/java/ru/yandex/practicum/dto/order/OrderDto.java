package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.order.OrderState;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {
    @NotNull
    UUID orderId;
    UUID shoppingCartId;
    @NotEmpty
    Map<UUID, Long> products;
    UUID paymentId;
    UUID deliveryId;
    OrderState state;
    Double deliveryWeight;
    Double deliveryVolume;
    Boolean fragile;
    Double totalPrice;
    Double deliveryPrice;
    Double productPrice;
}
