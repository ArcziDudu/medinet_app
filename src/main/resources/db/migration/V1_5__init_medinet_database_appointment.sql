CREATE TABLE appointment
(
    appointment_id SERIAL      NOT NULL PRIMARY KEY,
    patient_id     INT         NOT NULL UNIQUE,
    doctor_id      INT         NOT NULL UNIQUE,
    start_time     timestamp   NOT NULL,
    end_time       timestamp   NOT NULL,
    status         BOOLEAN     NOT NULL,
    note           TEXT,
    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id)
            REFERENCES patient (patient_id),
    CONSTRAINT fk_appointment_doctor
        FOREIGN KEY (doctor_id)
            REFERENCES doctor (doctor_id)
);
