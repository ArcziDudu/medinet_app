CREATE TABLE address
(
    address_id  SERIAL      NOT NULL,
    country     VARCHAR(32) NOT NULL,
    city        VARCHAR(50) NOT NULL,
    postal_code VARCHAR(40) NOT NULL,
    street      VARCHAR(50) NOT NULL,
    PRIMARY KEY (address_id)
);
