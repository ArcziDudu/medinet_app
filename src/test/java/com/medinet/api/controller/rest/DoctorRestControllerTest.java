package com.medinet.api.controller.rest;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorRestControllerTest {
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private DoctorService doctorService;
    @InjectMocks
    private DoctorRestController doctorRestController;
    private static final String DOCTOR_ID_RESULT = "/%s";

    @Test
    public void testOneDoctorById_doctorFound() {
        //given
        Integer doctorId = 1;
        DoctorDto expectedDoctorDto = new DoctorDto();
        expectedDoctorDto.setDoctorId(doctorId);
        expectedDoctorDto.setName("Test Doctor");

        when(doctorService.findDoctorById(doctorId)).thenReturn(expectedDoctorDto);

        //when
        ResponseEntity<DoctorDto> responseEntity = doctorRestController.oneDoctorById(doctorId);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedDoctorDto, responseEntity.getBody());
    }

    @Test
    public void testOneDoctorById_doctorIdIsNull() {
        //given
        Integer doctorId = null;

        //when
        ResponseEntity<DoctorDto> responseEntity = doctorRestController.oneDoctorById(doctorId);

        //then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testAllDoctors() {
        //given
        List<DoctorDto> expectedDoctors = Arrays.asList(
                new DoctorDto(),
                new DoctorDto()
        );

        when(doctorService.findAllDoctors()).thenReturn(expectedDoctors);

        //when
        List<DoctorDto> actualDoctors = doctorRestController.allDoctors();


        //then
        assertEquals(expectedDoctors.size(), actualDoctors.size());
        for (int i = 0; i < expectedDoctors.size(); i++) {
            assertEquals(expectedDoctors.get(i).getDoctorId(), actualDoctors.get(i).getDoctorId());
            assertEquals(expectedDoctors.get(i).getName(), actualDoctors.get(i).getName());
        }
    }

    @Test
    public void testGetAllDoctorsByPage() {
        //given
        Integer page = 0;
        List<DoctorDto> doctors = Arrays.asList(
                new DoctorDto(),
                new DoctorDto(),
                new DoctorDto()
        );
        Page<DoctorDto> expectedDoctorsPage = new PageImpl<>(doctors);

        when(doctorService.findAllDoctors(page)).thenReturn(expectedDoctorsPage);

        //when
        ResponseEntity<Page<DoctorDto>> responseEntity = doctorRestController.getAllDoctorsByPage(page);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Page<DoctorDto> actualDoctorsPage = responseEntity.getBody();

        assertNotNull(actualDoctorsPage);
        assertEquals(expectedDoctorsPage.getTotalElements(), actualDoctorsPage.getTotalElements());
        assertEquals(expectedDoctorsPage.getNumber(), actualDoctorsPage.getNumber());
        assertEquals(expectedDoctorsPage.getContent(), actualDoctorsPage.getContent());
    }

    @Test
    public void testCreateDoctor() {
        //given
        DoctorDto inputDoctorDto = new DoctorDto();
        DoctorEntity expectedDoctorEntity = new DoctorEntity();
        expectedDoctorEntity.setDoctorId(inputDoctorDto.getDoctorId());
        expectedDoctorEntity.setName(inputDoctorDto.getName());

        when(doctorMapper.mapFromDto(inputDoctorDto)).thenReturn(expectedDoctorEntity);
        when(doctorService.create(expectedDoctorEntity)).thenReturn(expectedDoctorEntity);

        //when
        ResponseEntity<DoctorDto> responseEntity = doctorRestController.createDoctor(inputDoctorDto);

        //then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        String API_CREATE_DOCTOR = "/new";
        assertEquals(API_CREATE_DOCTOR + DOCTOR_ID_RESULT.formatted(expectedDoctorEntity.getDoctorId()),
                Objects.requireNonNull(responseEntity.getHeaders().getLocation()).getPath());
    }

    @Test
    public void testDeleteDoctor() {
        //given
        Integer doctorId = 1;

        doNothing().when(doctorService).deleteById(doctorId);

        //when
        ResponseEntity<?> responseEntity = doctorRestController.deleteDoctor(doctorId);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}