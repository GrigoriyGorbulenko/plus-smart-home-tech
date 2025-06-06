package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@Data
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID productId;

    @Column(name = "product_name")
    String productName;

    String description;

    @Column(name = "image_src")
    String imageSrc;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "quantity_state")
    QuantityState quantityState;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "product_state")
    ProductState productState;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "product_category")
    ProductCategory productCategory;

    Float price;
}
