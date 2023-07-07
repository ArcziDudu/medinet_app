CREATE TABLE patient
(
    patient_id       SERIAL        NOT NULL PRIMARY KEY,
    name            VARCHAR(32)   NOT NULL,
    surname         VARCHAR(32)   NOT NULL,
    email           VARCHAR(32)   NOT NULL UNIQUE,
    phone           VARCHAR(32)   NOT NULL UNIQUE,
    address_id      INT           NOT NULL,
    CONSTRAINT fk_patient_address
        FOREIGN KEY (address_id)
            REFERENCES address (address_id)
);
