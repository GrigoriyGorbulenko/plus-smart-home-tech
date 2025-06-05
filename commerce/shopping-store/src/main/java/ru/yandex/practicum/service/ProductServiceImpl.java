package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.dto.shoppingstore.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;

import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.*;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<ProductDto> getProductsByType(ProductCategory category, Pageable pageable) {

        return productRepository.findAllByProductCategory(category, pageable)
                .stream()
                .map(ProductMapper::mapToProductDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        return ProductMapper.mapToProductDto(productRepository.save(ProductMapper.mapToProduct(productDto)));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = checkProductById(productDto.getProductId());

        String name = productDto.getProductName();
        if (name != null && !name.isBlank()) product.setProductName(name);

        String description = productDto.getDescription();
        if (description != null && !description.isBlank()) product.setDescription(description);

        String imageSrc = productDto.getImageSrc();
        if (imageSrc != null && !imageSrc.isBlank()) product.setImageSrc(imageSrc);

        ProductState productState = productDto.getProductState();
        if (productState != null) product.setProductState(productState);

        QuantityState quantityState = productDto.getQuantityState();
        if (quantityState != null) product.setQuantityState(quantityState);

        ProductCategory productCategory = productDto.getProductCategory();
        if (productCategory != null) product.setProductCategory(productCategory);

        Float price = productDto.getPrice();
        if (price != null) product.setPrice(price);

        return ProductMapper.mapToProductDto(product);
    }

    @Override
    @Transactional
    public Boolean removeProduct(UUID productId) {
        Product product = checkProductById(productId);
        if (product.getProductState().equals(ProductState.DEACTIVATE)) {
            return false;
        }

        product.setProductState(ProductState.DEACTIVATE);

        return true;
    }

    @Override
    @Transactional
    public Boolean setQuantityState(SetProductQuantityStateRequest request) {
        Product product = checkProductById(request.getProductId());
        if (product.getQuantityState().equals(request.getQuantityState())) {
            return false;
        }

        product.setQuantityState(request.getQuantityState());
        return true;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        return ProductMapper.mapToProductDto(checkProductById(productId));
    }

    private Product checkProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));
    }
}
