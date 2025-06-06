package ru.yandex.practicum.dto.order;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewOrderRequest {
    @Valid
    ShoppingCartDto shoppingCart;
    @NotNull
    AddressDto deliveryAddress;
}
