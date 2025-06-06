package ru.yandex.practicum.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PutMapping
    void addNewProduct(@RequestBody NewProductInWarehouseRequest newProductRequest) throws FeignException;

    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request) throws FeignException;

    @PostMapping("/return")
    void returnProducts(@RequestBody Map<UUID, Long> products) throws FeignException;

    @PostMapping("/check")
    BookedProductsDto checkProductsQuantity(@RequestBody ShoppingCartDto shoppingCartDto) throws FeignException;

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest assemblyRequest)
            throws FeignException;

    @PostMapping("/add")
    void addProductQuantity(@RequestBody AddProductToWarehouseRequest addProductQuantity) throws FeignException;

    @GetMapping("/address")
    AddressDto getWarehouseAddress() throws FeignException;
}
