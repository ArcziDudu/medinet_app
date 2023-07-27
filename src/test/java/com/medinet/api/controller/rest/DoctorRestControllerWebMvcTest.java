package com.medinet.api.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.util.EntityFixtures;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.medinet.api.controller.rest.DoctorRestController.API_CREATE_DOCTOR;
import static com.medinet.api.controller.rest.DoctorRestController.DOCTOR_ID_RESULT;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DoctorRestController.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@AutoConfigureMockMvc(addFilters = false)
public class DoctorRestControllerWebMvcTest {
    private MockMvc mockMvc;
    @MockBean
    private DoctorService doctorService;
    @MockBean
    private DoctorMapper doctorMapper;

    @Test
    public void oneDoctorById_ReturnsDoctorDto_WhenDoctorIdIsValid() throws Exception {
        // given
        Integer doctorId = 1;
        DoctorDto doctorDto = new DoctorDto();

        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);

        // when then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/doctor/" + doctorId) // replace with the correct url if different
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(doctorDto)));
    }

    @Test
    public void oneDoctorByIdReturnsNotFoundWhenDoctorIdIsInvalid() throws Exception {
        // given
        Integer doctorId = 1;

        when(doctorService.findDoctorById(doctorId)).thenReturn(null);

        // when then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/doctor/" + doctorId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createDoctorReturnsCreatedWhenDoctorDtoIsValid() throws Exception {
        // given
        DoctorDto doctorDto = new DoctorDto();

        DoctorEntity createdDoctor = EntityFixtures.someDoctor1();

        when(doctorMapper.mapFromDto(doctorDto)).thenReturn(createdDoctor);
        when(doctorService.create(createdDoctor)).thenReturn(createdDoctor);

        // when then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/doctor/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(doctorDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        API_CREATE_DOCTOR + DOCTOR_ID_RESULT.formatted(createdDoctor.getDoctorId())));
    }
}
