CREATE TABLE address
(
    address_id  SERIAL      NOT NULL,
    country     VARCHAR(32) NOT NULL,
    city        VARCHAR(32) NOT NULL,
    postal_code VARCHAR(32) NOT NULL,
    street      VARCHAR(32) NOT NULL,
    PRIMARY KEY (address_id)
);
