package com.medinet.util;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.security.UserEntity;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Set;

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
                .email("medinet@medinet.pl")
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
                        .id(2)
                        .email("medinet@medinet.pl")
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
                .dateOfAppointment(LocalDate.of(2023, 7, 20))
                .patient(new PatientEntity())
                .doctor(new DoctorEntity())
                .calendarId(1)
                .build();
    }
}
