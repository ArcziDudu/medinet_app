package com.medinet.infrastructure.repository;

import com.medinet.api.dto.DoctorDto;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.repository.jpa.DoctorJpaRepository;
import com.medinet.integration.PersistenceContainerTestConfiguration;
import com.medinet.util.EntityFixtures;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.awt.print.Pageable;
import java.util.*;
import java.util.stream.Collectors;

import static com.medinet.util.EntityFixtures.patient1;
import static com.medinet.util.EntityFixtures.someDoctor1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PersistenceContainerTestConfiguration.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DoctorRepositoryTest {
    @Mock
    private DoctorJpaRepository doctorJpaRepository;
    @Test
    public void testFindAllAvailableSpecialization() {
        // given
        DoctorEntity doctorEntity1 = EntityFixtures.someDoctor1();

        DoctorEntity doctorEntity2 = EntityFixtures.someDoctor2();
        List<DoctorEntity> allDoctors = Arrays.asList(doctorEntity1, doctorEntity2);

        Set<String> expectedSpecializations = new HashSet<>(Arrays.asList(
                doctorEntity1.getSpecialization(),
                doctorEntity2.getSpecialization()));


        when(doctorJpaRepository.findAll()).thenReturn(allDoctors);

        // when
        Set<String> result = doctorJpaRepository.findAll()
                .stream()
                .map(DoctorEntity::getSpecialization)
                .collect(Collectors.toSet());

        // then
        assertNotNull(result);
        assertEquals(expectedSpecializations, result);

        verify(doctorJpaRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllAvailableCities() {
        // given
        DoctorEntity doctorEntity1 = EntityFixtures.someDoctor1();

        DoctorEntity doctorEntity2 = EntityFixtures.someDoctor2();
        List<DoctorEntity> allDoctors = Arrays.asList(doctorEntity1, doctorEntity2);

        Set<String> expectedSpecializations = new HashSet<>(Arrays.asList(
                doctorEntity1.getAddress().getCity(),
                doctorEntity2.getAddress().getCity()));


        when(doctorJpaRepository.findAll()).thenReturn(allDoctors);

        // when
        Set<String> result = doctorJpaRepository.findAll()
                .stream()
                .map(a->a.getAddress().getCity())
                .collect(Collectors.toSet());
        // then
        assertNotNull(result);
        assertEquals(expectedSpecializations, result);
    }

    @Test
    public void testFindByEmail() {
        // given
        DoctorEntity doctorEntity = EntityFixtures.someDoctor1();

        when(doctorJpaRepository.findByEmail(doctorEntity.getEmail())).thenReturn(Optional.of(doctorEntity));

        // when
        Optional<DoctorEntity> result = doctorJpaRepository.findByEmail(doctorEntity.getEmail());

        // then
        assertTrue(result.isPresent());
        assertEquals(doctorEntity, result.get());

        verify(doctorJpaRepository, times(1)).findByEmail(doctorEntity.getEmail());

    }

    @Test
    public void testFindByEmailNotFound() {
        DoctorEntity doctorEntity = EntityFixtures.someDoctor1();
        when(doctorJpaRepository.findByEmail(doctorEntity.getEmail())).thenReturn(Optional.empty());

        // when
        Optional<DoctorEntity> result = doctorJpaRepository.findByEmail(doctorEntity.getEmail());

        // then
        assertFalse(result.isPresent());

        verify(doctorJpaRepository, times(1)).findByEmail(doctorEntity.getEmail());

    }


    @Test
    public void testFindAllDoctorsBySpecializationAndCity() {
        // given
        String doctorSpecialization = "Kadiolog";
        String doctorCity = "Kraków";

        PageRequest page = PageRequest.of(0, 10);

        DoctorEntity doctorEntity1 = EntityFixtures.someDoctor1();
        DoctorEntity doctorEntity2 = EntityFixtures.someDoctor2();

        List<DoctorEntity> allDoctors = Arrays.asList(doctorEntity1, doctorEntity2);

        Page<DoctorEntity> allDoctorsPage = new PageImpl<>(allDoctors, page, allDoctors.size());

        when(doctorJpaRepository.findAllDoctorsBySpecializationAndCity(doctorSpecialization, doctorCity, page))
                .thenReturn(allDoctorsPage);

        // when
        Page<DoctorEntity> doctors = doctorJpaRepository.findAllDoctorsBySpecializationAndCity(
                doctorSpecialization,
                doctorCity,
                page);

        // then
        assertNotNull(doctors);
        assertEquals(allDoctors, doctors.getContent());
        assertEquals(allDoctors.size(), doctors.getTotalElements());


        verify(doctorJpaRepository, times(1)).
                findAllDoctorsBySpecializationAndCity(doctorSpecialization, doctorCity, page);

    }

    @Test
    public void testFindAllDoctors(){

        PageRequest page = PageRequest.of(0, 10);

        DoctorEntity doctorEntity1 = EntityFixtures.someDoctor1();
        DoctorEntity doctorEntity2 = EntityFixtures.someDoctor2();

        List<DoctorEntity> allDoctors = Arrays.asList(doctorEntity1, doctorEntity2);

        Page<DoctorEntity> allDoctorsPage = new PageImpl<>(allDoctors, page, allDoctors.size());

        when(doctorJpaRepository.findAllDoctors(page))
                .thenReturn(allDoctorsPage);

        // when
        Page<DoctorEntity> doctors = doctorJpaRepository.findAllDoctors(page);

        // then
        assertNotNull(doctors);
        assertEquals(allDoctors, doctors.getContent());
        assertEquals(allDoctors.size(), doctors.getTotalElements());


        verify(doctorJpaRepository, times(1)).findAllDoctors(page);
    }
}