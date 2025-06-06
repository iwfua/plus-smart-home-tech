CREATE TABLE IF NOT EXISTS product (
                                       product_id UUID PRIMARY KEY,
                                       fragile BOOLEAN NOT NULL,
                                       width DOUBLE PRECISION NOT NULL,
                                       height DOUBLE PRECISION NOT NULL,
                                       depth DOUBLE PRECISION NOT NULL,
                                       weight DOUBLE PRECISION NOT NULL,
                                       quantity BIGINT NOT NULL
);