package com.medinet.infrastructure.repository;

import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.repository.jpa.AppointmentJpaRepository;
import com.medinet.integration.PersistenceContainerTestConfiguration;
import com.medinet.util.EntityFixtures;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PersistenceContainerTestConfiguration.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
class AppointmentRepositoryTest {

    private AppointmentJpaRepository appointmentJpaRepository;

    @Test
    void testSaveAppointment() {
        //given
        AppointmentEntity appointmentEntity = getAppointment();

        //when
        appointmentJpaRepository.save(appointmentEntity);
        List<AppointmentEntity> all = appointmentJpaRepository.findAll();

        assertThat(all).hasSize(1);
    }

    @Test
    void testRemoveAppointment() {
        //given
        AppointmentEntity appointmentEntity = getAppointment();
        appointmentJpaRepository.save(appointmentEntity);

        //when
        appointmentJpaRepository.deleteByAppointmentId(appointmentEntity.getAppointmentId());
        List<AppointmentEntity> all = appointmentJpaRepository.findAll();

        //then
        assertThat(all).isEmpty();
    }


    @Test
    void testFindAllByStatus() {
        //given
        String status = "upcoming";
        AppointmentEntity appointment1 = getAppointment(status);
        AppointmentEntity appointment2 = getAppointment("completed");
        appointment2.setUUID("uuid3");
        appointmentJpaRepository.save(appointment1);
        appointmentJpaRepository.save(appointment2);

        //when
        List<AppointmentEntity> appointmentsByStatus = appointmentJpaRepository.findAllByStatus(status);

        //then
        assertThat(appointmentsByStatus).hasSize(1);
        assertThat(appointmentsByStatus.get(0).getStatus()).isEqualTo(status);
    }

    @Test
    void testFindByDateOfAppointmentAndTimeOfVisit() {
        //given
        LocalDate dateOfAppointment = LocalDate.now();
        LocalTime timeOfVisit = LocalTime.of(13, 0);
        AppointmentEntity appointmentEntity = getAppointment(dateOfAppointment, timeOfVisit);
        appointmentJpaRepository.save(appointmentEntity);

        //when
        Optional<AppointmentEntity> foundAppointment = appointmentJpaRepository.findByDateOfAppointmentAndTimeOfVisit(dateOfAppointment, timeOfVisit);

        //then
        assertThat(foundAppointment).isPresent();
        assertThat(foundAppointment.get().getDateOfAppointment()).isEqualTo(dateOfAppointment);
        assertThat(foundAppointment.get().getTimeOfVisit()).isEqualTo(timeOfVisit);
    }

    private AppointmentEntity getAppointment(LocalDate dateOfAppointment, LocalTime timeOfVisit) {
        return AppointmentEntity.builder()
                .appointmentId(1)
                .dateOfAppointment(dateOfAppointment)
                .noteOfAppointment("test")
                .patient(EntityFixtures.patient1())
                .doctor(EntityFixtures.someDoctor1())
                .issueInvoice(OffsetDateTime.now())
                .calendarId(1)
                .UUID("uuid")
                .status("upcoming")
                .timeOfVisit(timeOfVisit)
                .build();
    }

    private AppointmentEntity getAppointment() {
        return AppointmentEntity.builder()
                .appointmentId(1)
                .dateOfAppointment(LocalDate.now())
                .noteOfAppointment("test")
                .patient(EntityFixtures.patient1())
                .doctor(EntityFixtures.someDoctor1())
                .issueInvoice(OffsetDateTime.now())
                .calendarId(1)
                .UUID("uuid1")
                .status("upcoming")
                .timeOfVisit(LocalTime.of(13, 0))
                .build();
    }

    private AppointmentEntity getAppointment(String status) {
        return AppointmentEntity.builder()
                .appointmentId(1)
                .dateOfAppointment(LocalDate.now())
                .noteOfAppointment("test")
                .patient(EntityFixtures.patient1())
                .doctor(EntityFixtures.someDoctor1())
                .issueInvoice(OffsetDateTime.now())
                .status(status)
                .calendarId(1)
                .UUID("uuid2")
                .timeOfVisit(LocalTime.of(13, 0))
                .build();
    }

}