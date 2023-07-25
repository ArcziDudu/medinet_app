package com.medinet.util;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.RequestDto;
import com.medinet.infrastructure.entity.*;
import com.medinet.infrastructure.security.UserEntity;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Set;

import static java.util.UUID.randomUUID;

@UtilityClass
public class EntityFixtures {


    public static DoctorEntity someDoctor1(){
        return DoctorEntity.builder()
                .doctorId(1)
                .name("Krystian")
                .surname("Medinet")
                .email("medinet@medinet.pl")
                .specialization("Kardiolog")
                .priceForVisit(new BigDecimal(200))
                .address(AddressEntity.builder()
                        .addressId(1)
                        .country("Poland")
                        .street("Polna 32")
                        .city("Kraków")
                        .postalCode("55-555")
                        .build())
                .appointments(Set.of())
                .calendars(Set.of())
                .opinions(Set.of())
                .user(UserEntity.builder()
                        .id(1)
                        .email("medinet@medinet.pl")
                        .active(true)
                        .password("test")
                        .build())
                .build();
    }
    public static DoctorEntity someDoctor2(){
        return DoctorEntity.builder()
                .doctorId(2)
                .name("Krystian")
                .surname("Medinet")
                .email("test@medinet.pl")
                .specialization("Ginekolog")
                .priceForVisit(new BigDecimal(200))
                .address(AddressEntity.builder()
                        .addressId(2)
                        .country("Poland")
                        .street("Polna 32")
                        .city("Warszawa")
                        .postalCode("55-555")
                        .build())
                .appointments(Set.of())
                .calendars(Set.of())
                .opinions(Set.of())
                .user(UserEntity.builder()
                        .email("medinet@medinet.pl")
                        .active(true)
                        .password("test")
                        .build())
                .build();
    }
    public static DoctorDto someDoctorDto(){
        return DoctorDto.builder()
                .doctorId(3)
                .name("Tester")
                .surname("testowy")
                .email("test@testowy.pl")
                .specialization("Ginekolog")
                .priceForVisit(new BigDecimal(200))
                .address(AddressEntity.builder()
                        .addressId(3)
                        .country("Test")
                        .street("Polna 32")
                        .city("Test")
                        .postalCode("55-555")
                        .build())
                .appointments(Set.of())
                .calendars(Set.of())
                .opinions(Set.of())
                .user(UserEntity.builder()
                        .email("test@testowy.pl")
                        .active(true)
                        .password("test")
                        .build())
                .build();
    }
    public static PatientEntity patient1(){
        return PatientEntity.builder()
                .patientId(1)
                .name("John")
                .email("john@example.com")
                .surname("Doe")
                .user(UserEntity.builder()
                        .id(3)
                        .password("test")
                        .email("john@example.com")
                        .active(true)
                        .build())
                .phoneNumber("+48 190 890 123")
                .address(AddressEntity.builder()
                        .addressId(3)
                        .country("Polska")
                        .street("123 Main St")
                        .city("New York")
                        .postalCode("55-151")
                        .build())
                .build();
    }
    public static PatientEntity patient2(){
        return PatientEntity.builder()
                .patientId(2)
                .name("Test")
                .email("john@test.com")
                .surname("Tester")
                .user(UserEntity.builder()
                        .id(4)
                        .password("test")
                        .email("john@test.com")
                        .active(true)
                        .build())
                .phoneNumber("+48 123 421 989")
                .address(AddressEntity.builder()
                        .addressId(4)
                        .country("Polska")
                        .street("123 Main Street")
                        .city("Kraków")
                        .postalCode("55-151")
                        .build())
                .build();
    }

    public static AppointmentDto someAppointment1(){
        return AppointmentDto.builder()
                .appointmentId(1)
                .timeOfVisit(LocalTime.of(10, 0))
                .status("done")
                .noteOfAppointment("First appointment note")
                .UUID("123e4567-e89b-12d3-a456-426614174000")
                .issueInvoice(OffsetDateTime.now())
                .dateOfAppointment(LocalDate.of(2023, 7, 26))
                .patient(new PatientEntity())
                .doctor(new DoctorEntity())
                .calendarId(1)
                .build();
    }

    public static RequestDto requestDto(){
        LocalDate localDate = LocalDate.now().plusDays(1);
        if(localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)||localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            localDate = localDate.plusDays(4);
        }
        return RequestDto.builder()
                .email("admin@admin.pl")
                .timeOfVisit(LocalTime.of(10,0))
                .dateOfAppointment(localDate)
                .doctorId(1)
                .build();
    }
    public static AppointmentEntity appointment(){
        LocalDate localDate = LocalDate.now().plusDays(1);
        if(localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)||localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            localDate = localDate.plusDays(4);
        }
        return AppointmentEntity.builder()
                .UUID(randomUUID().toString())
                .appointmentId(1)
                .calendarId(1)
                .status("done")
                .dateOfAppointment(localDate)
                .timeOfVisit(LocalTime.of(15,0))
                .patient(patient1())
                .doctor(someDoctor1())
                .issueInvoice(OffsetDateTime.now())
                .build();
    }
    public static AppointmentEntity appointment2(){
        LocalDate localDate = LocalDate.now().plusDays(1);
        if(localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)||localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            localDate = localDate.plusDays(4);
        }
        return AppointmentEntity.builder()
                .UUID(randomUUID().toString())
                .appointmentId(2)
                .calendarId(2)
                .status("upcoming")
                .dateOfAppointment(localDate)
                .timeOfVisit(LocalTime.of(15,0))
                .patient(patient1())
                .doctor(someDoctor1())
                .issueInvoice(OffsetDateTime.now())
                .build();
    }
    public static AppointmentEntity appointment3(){
        LocalDate localDate = LocalDate.now().plusDays(1);
        if(localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)||localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            localDate = localDate.plusDays(4);
        }
        return AppointmentEntity.builder()
                .UUID(randomUUID().toString())
                .appointmentId(3)
                .calendarId(1)
                .status("pending")
                .noteOfAppointment("test")
                .dateOfAppointment(localDate)
                .timeOfVisit(LocalTime.of(15,0))
                .patient(patient1())
                .doctor(someDoctor1())
                .issueInvoice(OffsetDateTime.now())
                .build();
    }

    public static OpinionEntity opinion(){
        return OpinionEntity.builder()
                .doctor(someDoctor2())
                .patient(patient1())
                .dateOfCreateOpinion(OffsetDateTime.now())
                .note("test opinion")
                .opinionId(1)
                .build();
    }
    public static OpinionEntity opinion2(){
        return OpinionEntity.builder()
                .doctor(someDoctor2())
                .patient(patient1())
                .dateOfCreateOpinion(OffsetDateTime.now())
                .note("test opinion2")
                .opinionId(2)
                .build();
    }
    public static OpinionEntity opinion3(){
        return OpinionEntity.builder()
                .doctor(someDoctor2())
                .patient(patient1())
                .dateOfCreateOpinion(OffsetDateTime.now())
                .note("test opinion3")
                .opinionId(3)
                .build();
    }
}
