package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.OpinionDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.OpinionService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.OpinionMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import com.medinet.util.EntityFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpinionControllerTest {
    @Mock
    private OpinionService opinionService;

    @Mock
    private PatientService patientService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private PatientMapper patientMapper;
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private OpinionMapper opinionMapper;

    @InjectMocks
    private OpinionController opinionController;

    @Mock
    private Principal principal;


    @Test
    public void testSendOpinion() {
        Integer doctorId = 1;
        String note = "This is a test opinion.";
        String userEmail = "test@example.com";

        PatientEntity patientEntity = EntityFixtures.patient1();
        DoctorEntity doctorEntity = EntityFixtures.someDoctor1();

        PatientDto patientDto = new PatientDto();
        DoctorDto doctorDto = new DoctorDto();

        OpinionDto opinionDto = new OpinionDto();
        opinionDto.setPatient(patientEntity);
        opinionDto.setDoctor(doctorEntity);
        opinionDto.setNote(note);

        when(principal.getName()).thenReturn(userEmail);
        when(patientService.findByEmail(userEmail)).thenReturn(patientDto);
        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);


        String result = opinionController.sendOpinion(doctorId, note, principal);

        verify(opinionService, times(1)).processOpinion(opinionMapper.mapFromDto(opinionDto));

        assertEquals("redirect:/booking?opinion=true", result);
    }

}