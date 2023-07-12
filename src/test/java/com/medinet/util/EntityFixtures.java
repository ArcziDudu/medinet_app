package com.medinet.util;

import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.security.UserEntity;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Set;

@UtilityClass
public class EntityFixtures {


    public static DoctorEntity someDoctor(PasswordEncoder passwordEncoder){
        return DoctorEntity.builder()
                .name("Krystian")
                .surname("Medinet")
                .email("medinet@medinet.pl")
                .specialization("Kardiolog")
                .priceForVisit(new BigDecimal(200))
                .address(AddressEntity.builder()
                        .country("Poland")
                        .street("Polna 32")
                        .city("Kraków")
                        .postalCode("55-555")
                        .build())
                .appointments(Set.of())
                .calendars(Set.of())
                .opinions(Set.of())
                .user(UserEntity.builder()
                        .email("medinet@medinet.pl")
                        .active(true)
                        .password(passwordEncoder.encode("test"))
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
                        .id(1)
                        .password("test")
                        .email("john@example.com")
                        .active(true)
                        .build())
                .phoneNumber("+48 190 890 123")
                .address(AddressEntity.builder()
                        .addressId(1)
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
                        .id(2)
                        .password("test")
                        .email("john@test.com")
                        .active(true)
                        .build())
                .phoneNumber("+48 123 421 989")
                .address(AddressEntity.builder()
                        .addressId(2)
                        .country("Polska")
                        .street("123 Main Street")
                        .city("Kraków")
                        .postalCode("55-151")
                        .build())
                .build();
    }
}
