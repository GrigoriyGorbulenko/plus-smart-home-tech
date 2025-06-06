CREATE TABLE IF NOT EXISTS payments (
  payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id UUID NOT NULL,
  state VARCHAR(50),
  total_payment NUMERIC(10, 2) NOT NULL,
  delivery_total NUMERIC(10, 2) NOT NULL,
  products_total NUMERIC(10, 2) NOT NULL
);