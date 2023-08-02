package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.OpinionDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.OpinionService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.OpinionMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OpinionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OpinionControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OpinionService opinionService;
    @MockBean
    private PatientService patientService;
    @MockBean
    private DoctorService doctorService;
    @MockBean
    private PatientMapper patientMapper;
    @MockBean
    private DoctorMapper doctorMapper;
    @MockBean
    private OpinionMapper opinionMapper;
    @MockBean
    private Principal principal;


    @Test
    public void testCreateNewOpinion() throws Exception {
        // given
        String email = "admin@admin.pl";
        int doctorId = 1;
        String note = "test note";
        PatientDto patient = new PatientDto();
        DoctorDto doctor = new DoctorDto();
        when(patientService.findByEmail(email)).thenReturn(patient);
        when(doctorService.findDoctorById(doctorId)).thenReturn(doctor);


        OpinionDto opinion = new OpinionDto();
        opinion.setPatient(patientMapper.mapFromDto(patient));
        opinion.setDoctor(doctorMapper.mapFromDto(doctor));
        opinion.setNote(note);
        opinion.setDateOfCreateOpinion(OffsetDateTime.now());

        // when
        mockMvc.perform(post("/opinion/new")
                        .param("doctorId", String.valueOf(doctorId))
                        .param("opinionNote", note)
                        .principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/booking?opinion=true"));
    }
}
