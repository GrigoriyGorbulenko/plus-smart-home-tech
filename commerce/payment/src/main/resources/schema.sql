CREATE TABLE IF NOT EXISTS payments (
  payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id UUID NOT NULL,
  state VARCHAR(50),
  total_payment DECIMAL NOT NULL,
  delivery_total DECIMAL NOT NULL,
  products_total DECIMAL NOT NULL
);