package com.medinet.business.services;

import com.medinet.business.dao.PatientDao;
import com.medinet.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private PatientDao patientDao;
    @InjectMocks
    private PatientService patientService;


    @Test
    public void testFindByIdThrowsException() {
        // Given
        int testId = 123;
        when(patientDao.findById(testId)).thenReturn(Optional.empty());

        // When then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> patientService.findById(testId));
        assertEquals("Could not find patient by id: [123]", exception.getMessage());
        verify(patientDao, times(1)).findById(testId);

    }

    @Test
    public void testFindByUserIdThrowsException() {
        // Given
        int testId = 123;
        when(patientDao.findByUserId(testId)).thenReturn(Optional.empty());

        // When then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> patientService.findByUserId(testId));
        assertEquals("Could not find patient by Userid: [123]", exception.getMessage());
        verify(patientDao, times(1)).findByUserId(testId);

    }

    @Test
    public void testFindByEmailThrowsException() {
        // Given
        String testEmail = "test@example.com";
        when(patientDao.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> patientService.findByEmail(testEmail));
        assertEquals("Could not find patient by email: [test@example.com]", exception.getMessage());
        verify(patientDao, times(1)).findByEmail(testEmail);

    }
}