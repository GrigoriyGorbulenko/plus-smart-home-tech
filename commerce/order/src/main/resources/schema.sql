CREATE TABLE IF NOT EXISTS orders (
  order_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_name VARCHAR(50),
  shopping_cart_id UUID,
  payment_id UUID,
  delivery_id UUID,
  state VARCHAR(50),
  delivery_weight DECIMAL,
  delivery_volume DECIMAL,
  fragile BOOLEAN,
  total_price DECIMAL,
  delivery_price DECIMAL,
  product_price DECIMAL
);

CREATE TABLE IF NOT EXISTS products (
  product_id UUID,
  quantity BIGINT,
  order_id UUID REFERENCES orders(order_id) ON DELETE CASCADE,
  PRIMARY KEY (product_id, order_id)
);