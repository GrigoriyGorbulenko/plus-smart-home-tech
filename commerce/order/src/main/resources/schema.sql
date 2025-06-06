CREATE TABLE IF NOT EXISTS orders (
  order_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_name VARCHAR(50),
  shopping_cart_id UUID,
  payment_id UUID,
  delivery_id UUID,
  state VARCHAR(50),
  delivery_weight NUMERIC(10, 2),
  delivery_volume NUMERIC(10, 2),
  fragile BOOLEAN,
  total_price NUMERIC(10, 2),
  delivery_price NUMERIC(10, 2),
  product_price NUMERIC(10, 2),
);

CREATE TABLE IF NOT EXISTS products (
  product_id UUID,
  quantity BIGINT,
  order_id UUID REFERENCES orders(order_id) ON DELETE CASCADE,
  PRIMARY KEY (product_id, order_id)
);