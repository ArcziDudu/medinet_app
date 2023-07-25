package com.medinet.business.services;

import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.PatientDao;
import com.medinet.domain.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private PatientDao patientDao;
    @InjectMocks
    private PatientService patientService;




    @Test
    public void testFindByIdThrowsException() {
        // Given
        Integer testId = 123;
        when(patientDao.findByUserId(testId)).thenReturn(Optional.empty());

        // When
        Executable executable = () -> patientService.findById(testId);

        // Then
        assertThrows(NotFoundException.class, executable);
    }
    @Test
    public void testFindByUserIdThrowsException() {
        // Given
        int testId = 123;
        when(patientDao.findByUserId(testId)).thenReturn(Optional.empty());

        // When
        Executable executable = () -> patientService.findByUserId(testId);

        // Then
        assertThrows(NotFoundException.class, executable);
    }

    @Test
    public void testFindByEmailThrowsException() {
        // Given
        String testEmail = "test@example.com";
        when(patientDao.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When
        Executable executable = () -> patientService.findByEmail(testEmail);

        // Then
        assertThrows(NotFoundException.class, executable);
    }
}