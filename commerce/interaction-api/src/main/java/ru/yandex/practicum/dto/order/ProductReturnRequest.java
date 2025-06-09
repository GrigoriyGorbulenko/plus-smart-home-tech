package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductReturnRequest {
    @NotNull
    UUID orderId;
    @NotNull
    Map<UUID, Long> products;
}
