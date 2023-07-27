CREATE TABLE patient
(
    patient_id SERIAL      NOT NULL PRIMARY KEY,
    user_id    INT         NOT NULL,
    name       VARCHAR(50) NOT NULL,
    surname    VARCHAR(50) NOT NULL,
    email      VARCHAR(45) NOT NULL UNIQUE,
    phone      VARCHAR(32) NOT NULL UNIQUE,
    address_id INT         NOT NULL,
    CONSTRAINT fk_patient_address
        FOREIGN KEY (address_id)
            REFERENCES address (address_id)
);
