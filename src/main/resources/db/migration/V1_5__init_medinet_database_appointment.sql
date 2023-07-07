CREATE TABLE appointment
(
    appointment_id      SERIAL                   NOT NULL PRIMARY KEY,
    patient_id          INT                      NOT NULL,
    doctor_id           INT                      NOT NULL,
    calendar_id         INT                      NOT NULL,
    time_of_visit       TIMESTAMP WITH TIME ZONE NOT NULL,
    time_of_issue       TIMESTAMP WITH TIME ZONE NOT NULL,
    date_of_appointment DATE                     NOT NULL,
    status              BOOLEAN                  NOT NULL,
    UUID                varchar(40)              not null UNIQUE,
    note                TEXT,
    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id)
            REFERENCES patient (patient_id),
    CONSTRAINT fk_appointment_doctor
        FOREIGN KEY (doctor_id)
            REFERENCES doctor (doctor_id)
);
