package com.medinet.business.services;

import com.medinet.business.dao.DoctorDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        NotFoundException exception = assertThrows(NotFoundException.class, () -> doctorService.findDoctorById(testId));
        assertEquals("Could not find doctor by doctorId: [123]", exception.getMessage());
        verify(doctorDao, times(1)).findDoctorById(testId);

    }

    @Test
    public void testDeleteByIdThrowsException() {
        // Given
        int testId = 123;
        when(doctorDao.findDoctorById(testId)).thenReturn(Optional.empty());

        // When then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> doctorService.deleteById(testId));
        assertEquals("Could not find doctor by id: [123]", exception.getMessage());
        verify(doctorDao, times(1)).findDoctorById(testId);
    }

    @Test
    public void testFindByEmailThrowsException() {
        // Given
        String testEmail = "test@example.com";
        when(doctorDao.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When tehn
        NotFoundException exception = assertThrows(NotFoundException.class, () -> doctorService.findByEmail(testEmail));
        assertEquals("Could not find doctor by email: [test@example.com]", exception.getMessage());
        verify(doctorDao, times(1)).findByEmail(testEmail);
    }

}