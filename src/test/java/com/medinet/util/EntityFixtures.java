package com.medinet.util;

import com.medinet.api.dto.DoctorDto;
import com.medinet.infrastructure.entity.AddressEntity;

import com.medinet.infrastructure.security.UserEntity;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.util.Set;

@UtilityClass

public class EntityFixtures {


    public static DoctorDto someDoctor(PasswordEncoder passwordEncoder){
        return DoctorDto.builder()
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
}
