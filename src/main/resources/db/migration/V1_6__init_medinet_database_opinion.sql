CREATE TABLE opinion
(
    opinion_id SERIAL    NOT NULL PRIMARY KEY,
    patient_id INT       NOT NULL UNIQUE,
    doctor_id  INT       NOT NULL UNIQUE,
    date       TIMESTAMP WITH TIME ZONE not null,
    note       TEXT,
    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id)
            REFERENCES patient (patient_id),
    CONSTRAINT fk_appointment_doctor
        FOREIGN KEY (doctor_id)
            REFERENCES doctor (doctor_id)
);
