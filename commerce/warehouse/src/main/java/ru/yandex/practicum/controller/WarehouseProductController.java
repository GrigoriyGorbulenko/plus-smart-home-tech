package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.service.WarehouseService;



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

    @PostMapping("/check")
    public BookedProductsDto checkProductsQuantity(@Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        log.info("Проверка наличия товаров из корзины");
        return productService.checkProductsQuantity(shoppingCartDto);
    }

    @PostMapping("/add")
    public void addProductQuantity(@Valid @RequestBody AddProductToWarehouseRequest addProductQuantity) {
        log.info("Добавление товара с id");
        productService.addProductQuantity(addProductQuantity);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("Получение адреса склада");
        return productService.getWarehouseAddress();
    }
}
