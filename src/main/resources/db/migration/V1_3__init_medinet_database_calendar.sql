CREATE TABLE calendar
(
    calendar_id SERIAL NOT NULL PRIMARY KEY,
    doctor_id   int,
    date        date   NOT NULL,
    hours       time[] NOT NULL,
    CONSTRAINT fk_calendar_doctor
        foreign key (doctor_id)
            references doctor (doctor_id)
);

