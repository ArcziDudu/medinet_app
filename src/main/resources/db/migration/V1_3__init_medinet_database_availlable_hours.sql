CREATE TABLE availability
(
    availables_id   SERIAL      NOT NULL PRIMARY KEY,
    doctor_id       int         NOT NULL,
    day_of_the_week varchar(20) NOT NULL UNIQUE,
    hours           text[]        NOT NULL,
    CONSTRAINT fk_hours_address
        FOREIGN KEY (doctor_id)
            REFERENCES doctor (doctor_id)
);

