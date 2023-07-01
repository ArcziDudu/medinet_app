CREATE TABLE doctor
(
    doctor_id       SERIAL        NOT NULL PRIMARY KEY,
    name            VARCHAR(32)   NOT NULL,
    surname         VARCHAR(32)   NOT NULL,
    email           VARCHAR(32)   NOT NULL UNIQUE,
    price_for_visit DECIMAL(7, 2) NOT NULL,
    specialization  VARCHAR(15)   NOT NULL,
    address_id      INT           NOT NULL,
    CONSTRAINT fk_doctors_address
        FOREIGN KEY (address_id)
            REFERENCES address (address_id)
);

