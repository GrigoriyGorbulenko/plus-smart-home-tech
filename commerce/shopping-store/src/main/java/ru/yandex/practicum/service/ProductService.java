package ru.yandex.practicum.service;



import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.dto.shoppingstore.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;


import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductDto> getProductsByType(ProductCategory category, Pageable pageable);

    ProductDto addProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean removeProduct(UUID productId);

    Boolean setQuantityState(SetProductQuantityStateRequest setProductQuantityState);

    ProductDto getProductById(UUID productId);
}
