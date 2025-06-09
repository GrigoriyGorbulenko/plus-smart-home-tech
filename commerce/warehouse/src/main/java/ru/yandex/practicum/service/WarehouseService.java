package ru.yandex.practicum.service;


import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProduct(NewProductInWarehouseRequest newProductRequest);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void returnProducts(Map<UUID, Long> products);

    BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto);

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyRequest);

    void addProductQuantity(AddProductToWarehouseRequest addProductQuantity);

    AddressDto getWarehouseAddress();
}
