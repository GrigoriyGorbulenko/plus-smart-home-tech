package ru.yandex.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.enums.shopping_store.ProductCategory;
import ru.yandex.practicum.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByProductCategory(ProductCategory category, Pageable pageable);
    Product findByProductCategory(ProductCategory category);
}
