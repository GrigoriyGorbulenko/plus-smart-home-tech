package ru.yandex.practicum.dto.shoppingstore;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.shopping_store.ProductCategory;
import ru.yandex.practicum.enums.shopping_store.ProductState;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    UUID productId;
    @NotBlank
    String productName;
    @NotBlank
    String description;
    String imageSrc;
    @NotNull
    QuantityState quantityState;
    @NotNull
    ProductState productState;
    ProductCategory productCategory;
    @Min(value = 1)
    @NotNull
    Float price;
}
