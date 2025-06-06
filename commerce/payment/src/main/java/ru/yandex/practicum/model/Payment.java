package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.payment.PaymentState;

import java.util.UUID;

@Data
@Entity
@Table(name = "delivery_address")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID paymentId;

    @Column(name = "order_id")
    UUID orderId;

    @Enumerated(value = EnumType.STRING)
    PaymentState state;

    @Column(name = "total_payment")
    Double totalPayment;

    @Column(name = "delivery_total")
    Double deliveryTotal;

    @Column(name = "products_total")
    Double productsTotal;
}
