package ru.yandex.practicum.feign;


import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.shoppingstore.ProductDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username) throws FeignException;

    @PutMapping
    ShoppingCartDto addProductsToCart(@RequestParam String username,
                                      @RequestBody Map<UUID, Long> additionalProperties) throws FeignException;

    @DeleteMapping
    void deactivateShoppingCart(@RequestParam String username) throws FeignException;

    @PostMapping("/remove")
    ShoppingCartDto removeShoppingCart(@RequestParam String username,
                                                @RequestBody List<UUID> productIds) throws FeignException;

    @PostMapping("/change-quantity")
    ProductDto changeProductQuantity(@RequestParam String username,
                                     @RequestBody ChangeProductQuantityRequest request) throws FeignException;
}
