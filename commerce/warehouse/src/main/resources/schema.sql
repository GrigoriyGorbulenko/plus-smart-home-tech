CREATE TABLE IF NOT EXISTS warehouse_product (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN,
    width DECIMAL NOT NULL,
    height DECIMAL NOT NULL,
    depth DECIMAL NOT NULL,
    weight DECIMAL NOT NULL,
    quantity BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_booking (
    order_id UUID PRIMARY KEY,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS booking_products (
    order_booking_id UUID REFERENCES order_booking(order_id),
    product_id UUID REFERENCES warehouse_product(product_id),
    quantity BIGINT,
    PRIMARY KEY (order_booking_id, product_id)
);