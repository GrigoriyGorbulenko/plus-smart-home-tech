CREATE TABLE IF NOT EXISTS shopping_carts (
shopping_cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_name VARCHAR(50) UNIQUE NOT NULL,
state VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_products (
product_id UUID,
quantity BIGINT,
shopping_cart_id UUID REFERENCES shopping_carts(shopping_cart_id) ON DELETE CASCADE,
PRIMARY KEY (product_id, shopping_cart_id)
);