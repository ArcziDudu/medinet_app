package com.medinet.infrastructure.repository;

import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.repository.jpa.OpinionJpaRepository;
import com.medinet.integration.configuration.PersistenceContainerTestConfiguration;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PersistenceContainerTestConfiguration.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
class OpinionRepositoryTest {

    private OpinionJpaRepository opinionJpaRepository;


    @Test
    @DisplayName("Should save the opinion and return the corresponding DTO")
    void saveOpinionAndReturnDto() {
        OpinionEntity opinion = getOpinionEntity();

        OpinionEntity expectedDto = OpinionEntity.builder()
                .opinionId(1)
                .dateOfCreateOpinion(opinion.getDateOfCreateOpinion())
                .note(opinion.getNote())
                .patient(opinion.getPatient())
                .doctor(opinion.getDoctor())
                .build();

        OpinionEntity savedDto = opinionJpaRepository.save(opinion);
        List<OpinionEntity> all = opinionJpaRepository.findAll();

        assertThat(savedDto).isEqualTo(expectedDto);
        assertThat(all).hasSize(1);
    }

    private static OpinionEntity getOpinionEntity() {
        return OpinionEntity.builder()
                .opinionId(1)
                .dateOfCreateOpinion(OffsetDateTime.now())
                .note("Test opinion")
                .patient(PatientEntity.builder()
                        .patientId(1)
                        .name("John")
                        .email("john@example.com")
                        .surname("Doe")
                        .phoneNumber("1234567890")
                        .address(AddressEntity.builder()
                                .addressId(1)
                                .street("123 Main St")
                                .city("New York")
                                .postalCode("55-151")
                                .build())
                        .build())
                .doctor(DoctorEntity.builder()
                        .doctorId(1)
                        .name("Dr. Smith")
                        .surname("Smith")
                        .email("smith@example.com")
                        .priceForVisit(BigDecimal.valueOf(100))
                        .specialization("Cardiology")
                        .address(AddressEntity.builder()
                                .addressId(2)
                                .street("456 Elm St")
                                .city("Los Angeles")
                                .postalCode("55-789")
                                .build())
                        .build())
                .build();
    }

}
