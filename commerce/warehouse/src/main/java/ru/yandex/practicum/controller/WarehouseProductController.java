package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping(path = "/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseProductController {
    private final WarehouseService productService;

    @PutMapping
    public void addNewProduct(@Valid @RequestBody NewProductInWarehouseRequest newProductRequest) {
        log.info("Добавление нового товара на склад");
        productService.addNewProduct(newProductRequest);
    }

    @PostMapping("/shipped")
    public void shippedToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request) {
        log.info("Передача заказа в доставку");
        productService.shippedToDelivery(request);
    }

    @PostMapping("/return")
    public void returnProducts(@RequestBody Map<UUID, Long> products) {
        log.info("Возврат продуктов на склад");
        productService.returnProducts(products);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductsQuantity(@Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        log.info("Проверка наличия товаров из корзины");
        return productService.checkProductsQuantity(shoppingCartDto);
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductsForOrder(
            @Valid @RequestBody AssemblyProductsForOrderRequest assemblyRequest) {
        log.info("Сборка заказа");
        return productService.assemblyProductsForOrder(assemblyRequest);
    }

    @PostMapping("/add")
    public void addProductQuantity(@Valid @RequestBody AddProductToWarehouseRequest addProductQuantity) {
        log.info("Добавление товара");
        productService.addProductQuantity(addProductQuantity);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("Получение адреса склада");
        return productService.getWarehouseAddress();
    }
}
