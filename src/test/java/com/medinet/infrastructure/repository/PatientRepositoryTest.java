package com.medinet.infrastructure.repository;

import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.repository.jpa.PatientJpaRepository;
import com.medinet.integration.configuration.PersistenceContainerTestConfiguration;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.medinet.util.EntityFixtures.patient1;
import static com.medinet.util.EntityFixtures.patient2;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PersistenceContainerTestConfiguration.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
class PatientRepositoryTest {
    private PatientJpaRepository patientJpaRepository;

    @Test
    void thatShouldFindByIdCorrect() {
        // given
        PatientEntity patientEntity = patient1();

        patientEntity = patientJpaRepository.save(patientEntity);

        // when
        Optional<PatientEntity> patientById = patientJpaRepository.findById(patientEntity.getPatientId());

        // then
        assertThat(patientById).isPresent();
        assertThat(patientById.get().getPatientId()).isEqualTo(patientEntity.getPatientId());
        assertThat(patientById.get().getName()).isEqualTo(patientEntity.getName());
        assertThat(patientById.get().getEmail()).isEqualTo(patientEntity.getEmail());
        assertThat(patientById.get().getPhoneNumber()).isEqualTo(patientEntity.getPhoneNumber());
    }

    @Test
    void findByIdWhenPatientIdDoesNotExist() {
        int nonExistingPatientId = 100;

        Optional<PatientEntity> result = patientJpaRepository.findById(nonExistingPatientId);

        assertThat(result).isEmpty();
    }

    @Test
    void thatShouldReturnAllPatients() {
        // given
        PatientEntity patientEntity1 = patient1();
        PatientEntity patientEntity2 = patient2();


        patientJpaRepository.saveAll(Arrays.asList(patientEntity1, patientEntity2));

        // when
        List<PatientEntity> allPatients = patientJpaRepository.findAll();

        // then
        assertThat(allPatients).hasSize(2);

        assertThat(allPatients)
                .extracting(PatientEntity::getName)
                .containsExactlyInAnyOrder(patientEntity1.getName(), patientEntity2.getName());

    }

    @Test
    void thatSavePatientCorrect() {
        // given
        PatientEntity patientEntity = patient1();

        // when
        patientJpaRepository.save(patientEntity);

        // then
        List<PatientEntity> allPatients = patientJpaRepository.findAll();
        assertThat(allPatients).hasSize(1);

        PatientEntity savedPatient = allPatients.get(0);
        assertThat(savedPatient.getName()).isEqualTo(patientEntity.getName());
        assertThat(savedPatient.getEmail()).isEqualTo(patientEntity.getEmail());
        assertThat(savedPatient.getPhoneNumber()).isEqualTo(patientEntity.getPhoneNumber());
    }

    @Test
    void testExistsByPhoneNumber() {
        // given
        PatientEntity patientEntity = patient1();

        patientJpaRepository.save(patientEntity);

        // when
        boolean exists = patientJpaRepository.existsByPhoneNumber("+48 190 890 123");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void thatDoesNotExistByPhoneNumber() {
        // given
        PatientEntity patientEntity = patient2();

        patientJpaRepository.save(patientEntity);

        // when
        boolean exists = patientJpaRepository.existsByPhoneNumber("123456789");

        // then
        assertThat(exists).isFalse();
    }
    @Test
    void testFindByUserId() {
        // given
        PatientEntity patientEntity = patient1();

        patientJpaRepository.save(patientEntity);

        // when
        Optional<PatientEntity> byUserId = patientJpaRepository.findByUserId(patientEntity.getUser().getId());

        // then
        assertThat(byUserId).isPresent();
        assertThat(byUserId.get().getName()).isEqualTo(patientEntity.getName());
        assertThat(byUserId.get().getUser().getId()).isEqualTo(patientEntity.getUser().getId());
    }

    @Test
    void testFindByNonExistingUserId() {
        // given
        PatientEntity patientEntity = patient1();

        patientJpaRepository.save(patientEntity);

        // when
        Optional<PatientEntity> byUserId = patientJpaRepository.findByUserId(1231);

        // then
        assertThat(byUserId).isNotPresent();
    }
    @Test
    void thatShouldFindByEmail() {
        // given
        PatientEntity patientEntity = patient1();

        patientJpaRepository.save(patientEntity);

        // when
        Optional<PatientEntity> byEmail = patientJpaRepository.findByEmail("john@example.com");

        // then
        assertThat(byEmail).isPresent();
        assertThat(byEmail.get().getName()).isEqualTo(patientEntity.getName());
        assertThat(byEmail.get().getEmail()).isEqualTo(patientEntity.getEmail());
    }

    @Test
    void testFindByNonExistingEmail() {
        // given
        PatientEntity patientEntity = patient2();

        patientJpaRepository.save(patientEntity);

        // when
        Optional<PatientEntity> byEmail = patientJpaRepository.findByEmail("jane@example.com");

        // then
        assertThat(byEmail).isNotPresent();
    }
}