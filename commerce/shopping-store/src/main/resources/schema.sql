CREATE TABLE IF NOT EXISTS products (
  product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  product_name VARCHAR(100) NOT NULL,
  description VARCHAR(1200) NOT NULL,
  image_src VARCHAR(500),
  quantity_state VARCHAR (50) NOT NULL,
  product_state VARCHAR (50) NOT NULL,
  product_category VARCHAR (50) NOT NULL,
  price numeric(10, 2) NOT NULL
);