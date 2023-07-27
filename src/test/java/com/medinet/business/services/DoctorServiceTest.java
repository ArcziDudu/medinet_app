package com.medinet.business.services;

import com.medinet.business.dao.DoctorDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {
    @Mock
    private DoctorDao doctorDao;
    @Mock
    private CalendarService calendarService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private DoctorService doctorService;

    @Test
    public void testFindDoctorByIdThrowsException() {
        // Given
        int testId = 123;
        when(doctorDao.findDoctorById(testId)).thenReturn(Optional.empty());

        // When
        Executable executable = () -> doctorService.findDoctorById(testId);

        // Then
        assertThrows(NotFoundException.class, executable);
    }

    @Test
    public void testDeleteByIdThrowsException() {
        // Given
        int testId = 123;
        when(doctorDao.findDoctorById(testId)).thenReturn(Optional.empty());

        // When
        Executable executable = () -> doctorService.deleteById(testId);

        // Then
        assertThrows(NotFoundException.class, executable);
    }

    @Test
    public void testFindByEmailThrowsException() {
        // Given
        String testEmail = "test@example.com";
        when(doctorDao.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When
        Executable executable = () -> doctorService.findByEmail(testEmail);

        // Then
        assertThrows(NotFoundException.class, executable);
    }

}