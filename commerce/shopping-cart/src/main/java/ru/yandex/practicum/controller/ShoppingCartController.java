package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ShoppingCartDto getActiveShoppingCartOfUser(@RequestParam String username) {
        log.info("Получение корзины {}", username);
        return shoppingCartService.getShoppingCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProductsToCart(@RequestParam String username, @RequestBody Map<UUID, Long> newProducts) {
        log.info("Добавление товара в корзину {}", username);

        return shoppingCartService.addProductToCart(username, newProducts);
    }

    @DeleteMapping
    public void deactivateShoppingCart(@RequestParam String username) {
        log.info("Деактивация корзины товаров");
        shoppingCartService.deactivateShoppingCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeShoppingCart(@RequestParam String username, @RequestBody List<UUID> productIds) {
        log.info("Удаление продуктов {} из корзины", productIds);
        return shoppingCartService.removeShoppingCart(username, productIds);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                                 @Valid @RequestBody ChangeProductQuantityRequest request) {
        log.info("Изменение количества товара в корзине");
        return shoppingCartService.changeProductQuantity(username, request);
    }
}
