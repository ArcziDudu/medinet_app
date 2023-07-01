CREATE TABLE calendar
(
    calendar_id SERIAL NOT NULL PRIMARY KEY,
    doctor_id   int    not null,
    date        date   NOT NULL,
    hours       text[] NOT NULL,
    CONSTRAINT fk_calendar_doctor
        foreign key (doctor_id)
            references doctor (doctor_id)
);

