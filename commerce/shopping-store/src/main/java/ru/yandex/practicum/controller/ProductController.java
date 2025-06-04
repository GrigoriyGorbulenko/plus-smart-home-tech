package ru.yandex.practicum.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingstore.Pageable;
import ru.yandex.practicum.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.dto.shoppingstore.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.service.ProductService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/shopping-store")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public List<ProductDto> getProductsByType(@RequestParam ProductCategory category, @Valid Pageable pageableRequest) {
        log.info("Получение категории = {}", category);
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), Sort.by(pageableRequest.getSort().toArray(new String[0])));
        return productService.getProductsByType(category, pageRequest);
    }

    @PutMapping
    public ProductDto addProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Добавление товара c productName = {}", productDto.getProductName());
        return productService.addProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProduct(@RequestBody UUID productId) {
        log.info("Деактивация товара с id = {}", productId);
        return productService.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    public Boolean setQuantityState(@Valid SetProductQuantityStateRequest request) {
        return productService.setQuantityState(request);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        log.info("Получение информации о товаре с id = {}", productId);
        return productService.getProductById(productId);
    }
}
