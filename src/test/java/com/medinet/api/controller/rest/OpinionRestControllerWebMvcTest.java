package com.medinet.api.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.OpinionDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.OpinionService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
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

import java.util.ArrayList;
import java.util.List;

import static com.medinet.api.controller.rest.OpinionRestController.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OpinionRestController.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@AutoConfigureMockMvc(addFilters = false)
public class OpinionRestControllerWebMvcTest {
    private MockMvc mockMvc;
    @MockBean
    private OpinionService opinionService;
    @MockBean
    private PatientService patientService;
    @MockBean
    private PatientMapper patientMapper;
    @MockBean
    private DoctorService doctorService;
    @MockBean
    private DoctorMapper doctorMapper;

    @Test
    public void allOpinionsByPatientReturnsOpinionsWhenPatientIdIsValid() throws Exception {
        // given
        Integer patientId = 1;
        List<OpinionDto> opinions = new ArrayList<>();


        when(opinionService.findAll()).thenReturn(opinions);

        // when then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/opinion/patient/"  + patientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(opinions)));
    }

    @Test
    public void allOpinionsByDoctorReturnsOpinionsWhenDoctorIdIsValid() throws Exception {
        // given
        Integer doctorId = 1;
        List<OpinionDto> opinions = new ArrayList<>();

        when(opinionService.findAll()).thenReturn(opinions);

        // when then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/opinion/doctor/"+ doctorId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(opinions)));
    }
    @Test
    public void createOpinionReturnsOkWhenPatientIdAndDoctorIdAreValid() throws Exception {
        // given
        Integer patientId = 1;
        Integer doctorId = 2;
        String opinion = "Test opinion";
        OpinionDto opinionDto = new OpinionDto();

        when(doctorService.findDoctorById(doctorId)).thenReturn(new DoctorDto());
        when(patientService.findById(patientId)).thenReturn(new PatientDto());
        when(opinionService.processOpinion(any(OpinionEntity.class))).thenReturn(opinionDto);

        // when then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/opinion/create/"+ patientId + "/" + doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(opinion))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(opinionDto)));
    }
}
