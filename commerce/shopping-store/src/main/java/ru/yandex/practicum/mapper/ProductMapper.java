package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.model.Product;

import java.util.List;

@UtilityClass
public class ProductMapper {
    public Product mapToProduct(ProductDto productDto) {
        return Product.builder()
                .productId(productDto.getProductId())
                .productName(productDto.getProductName())
                .description(productDto.getDescription())
                .productCategory(productDto.getProductCategory())
                .imageSrc(productDto.getImageSrc())
                .productState(productDto.getProductState())
                .quantityState(productDto.getQuantityState())
                .price(productDto.getPrice())
                .build();
    }

    public ProductDto mapToProductDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .productCategory(product.getProductCategory())
                .imageSrc(product.getImageSrc())
                .productState(product.getProductState())
                .quantityState(product.getQuantityState())
                .price(product.getPrice())
                .build();
    }

    public List<ProductDto> mapToProductDto(List<Product> products) {
        return products.stream().map(ProductMapper::mapToProductDto).toList();
    }
}
