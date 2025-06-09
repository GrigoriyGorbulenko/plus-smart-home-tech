package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Entity
@Table(name = "warehouse_product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    UUID productId;

    Boolean fragile;

    Double width;

    Double height;

    Double depth;

    Double weight;

    Long quantity;
}
