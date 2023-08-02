package com.medinet.api.controller.rest;

import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.OpinionDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.OpinionService;
import com.medinet.business.services.PatientService;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpinionRestControllerTest {
    @Mock
    private OpinionService opinionService;
    @Mock
    private PatientService patientService;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private DoctorService doctorService;
    @Mock
    private DoctorMapper doctorMapper;
    @InjectMocks
    private OpinionRestController opinionRestController;

    @Test
    public void testAllOpinions() {
        //given
        List<OpinionDto> expectedOpinions = Arrays.asList(
                giveOpinion(1, "test1"),
                giveOpinion(2, "test2"),
                giveOpinion(3, "test3")
        );

        when(opinionService.findAll()).thenReturn(expectedOpinions);

        //when
        List<OpinionDto> actualOpinions = opinionRestController.allOpinions();

        //then
        assertEquals(expectedOpinions.size(), actualOpinions.size());
        for (int i = 0; i < expectedOpinions.size(); i++) {
            assertEquals(expectedOpinions.get(i).getOpinionId(), actualOpinions.get(i).getOpinionId());
            assertEquals(expectedOpinions.get(i).getNote(), actualOpinions.get(i).getNote());
        }
    }

    @Test
    public void testAllOpinionsByPatient() {
        //given
        Integer patientId = 1;
        PatientEntity patient = new PatientEntity();
        patient.setPatientId(patientId);

        List<OpinionDto> allOpinions = Arrays.asList(
                giveOpinion(1, "test1", patient),
                giveOpinion(1, "test2", patient)
        );

        List<OpinionDto> expectedOpinions = allOpinions.stream()
                .filter(opinion -> opinion.getPatient().getPatientId().equals(patientId))
                .toList();

        when(opinionService.findAll()).thenReturn(allOpinions);

        //when
        List<OpinionDto> actualOpinions = opinionRestController.allOpinionsByPatient(patientId);

        //then
        assertEquals(expectedOpinions.size(), actualOpinions.size());
        for (int i = 0; i < expectedOpinions.size(); i++) {
            assertEquals(expectedOpinions.get(i).getOpinionId(), actualOpinions.get(i).getOpinionId());
            assertEquals(expectedOpinions.get(i).getNote(), actualOpinions.get(i).getNote());
        }
    }

    @Test
    public void testAllOpinionsByDoctor() {
        //given
        Integer doctorId = 1;
        DoctorEntity doctor = new DoctorEntity();
        doctor.setDoctorId(doctorId);
        DoctorEntity doctor2 = new DoctorEntity();
        doctor.setDoctorId(5);

        List<OpinionDto> allOpinions = Arrays.asList(
                giveOpinion(1, "test", doctor),
                giveOpinion(2, "test2", doctor)

        );

        List<OpinionDto> expectedOpinions = allOpinions.stream()
                .filter(opinion -> opinion.getDoctor().getDoctorId().equals(doctorId))
                .toList();

        when(opinionService.findAll()).thenReturn(allOpinions);

        ///when
        List<OpinionDto> actualOpinions = opinionRestController.allOpinionsByDoctor(doctorId);

        //then
        assertEquals(expectedOpinions.size(), actualOpinions.size());
        for (int i = 0; i < expectedOpinions.size(); i++) {
            assertEquals(expectedOpinions.get(i).getOpinionId(), actualOpinions.get(i).getOpinionId());
            assertEquals(expectedOpinions.get(i).getNote(), actualOpinions.get(i).getNote());
        }
    }

    @Test
    public void testCreateDoctor() {
        //given
        Integer patientId = 1;
        Integer doctorId = 1;
        String opinionText = "Great doctor!";

        PatientEntity patient = new PatientEntity();
        PatientDto patientDto = new PatientDto();
        patient.setPatientId(patientId);
        patientDto.setPatientId(patientId);


        DoctorEntity doctor = new DoctorEntity();
        DoctorDto doctorDto = new DoctorDto();
        doctor.setDoctorId(doctorId);
        doctorDto.setDoctorId(doctorId);

        OpinionDto opinionDto = giveOpinion(1, "test", doctor);
        opinionDto.setDateOfCreateOpinion(OffsetDateTime.now());
        opinionDto.setPatient(patient);

        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);
        when(patientService.findById(patientId)).thenReturn(patientDto);
        when(opinionService.processOpinion(any(OpinionEntity.class))).thenReturn(opinionDto);

        //when
        ResponseEntity<?> responseEntity = opinionRestController.createOpinion(patientId, doctorId, opinionText);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(opinionDto, responseEntity.getBody());
    }

    @Test
    public void testCreateDoctor_NotFoundException() {
        //given
        Integer patientId = 1;
        Integer doctorId = 1;
        String opinionText = "Great doctor!";
        String exceptionMessage = "ID not found";

        when(doctorService.findDoctorById(doctorId)).thenThrow(new NotFoundException(exceptionMessage));

        //when
        ResponseEntity<?> responseEntity = opinionRestController.createOpinion(patientId, doctorId, opinionText);

        //then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(exceptionMessage, responseEntity.getBody());
    }

    private OpinionDto giveOpinion(int id, String test, DoctorEntity doctor) {
        return OpinionDto.builder()
                .opinionId(id)
                .note(test)
                .doctor(doctor)
                .build();
    }

    private OpinionDto giveOpinion(Integer id, String note) {
        return OpinionDto.builder()
                .opinionId(id)
                .note(note)
                .build();
    }

    private OpinionDto giveOpinion(Integer id, String note, PatientEntity patientEntity) {
        return OpinionDto.builder()
                .opinionId(id)
                .note(note)
                .patient(patientEntity)
                .build();
    }
}