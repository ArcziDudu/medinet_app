package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.util.EntityFixtures;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Formatter;
import java.util.Map;
import java.util.TreeSet;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DoctorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DoctorControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;
    @MockBean
    private DoctorMapper doctorMapper;
    @Mock
    private Model model;

    @MockBean
    private AppointmentService appointmentService;

    @Mock
    private Principal principal;

    private Map<String, Object> mockData;

    @Mock
    private Formatter formatter;
    @Mock
    private DoctorController doctorController;

    @Test
    public void testShowDoctorDetailsPage() throws Exception {
        // given
        Integer doctorId = 1;
        DoctorEntity doctorEntity = EntityFixtures.someDoctor1();
        DoctorDto doctorDto = getDoctorDto(doctorEntity);
        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);

        // when
        mockMvc.perform(get("/specialist/details/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(view().name("doctorDetails"))
                .andExpect(model().attributeExists("doctor"))
                .andExpect(model().attribute("doctor", doctorDto))
                .andExpect(model().attributeExists("dateFormatter"))
                .andExpect(model().attributeExists("polishDayFormatter"));
    }


    @Test
    public void testGetUser() throws Exception {
        // given
        Integer userId = 1;
        DoctorEntity doctorEntity = EntityFixtures.someDoctor1();
        DoctorDto doctorDto = getDoctorDto(doctorEntity);
        when(doctorMapper.mapFromEntity(doctorEntity)).thenReturn(doctorDto);
        doctorDto.setDoctorId(userId);

        when(doctorService.findDoctorById(userId)).thenReturn(doctorDto);


        mockMvc.perform(get("/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("myAccountDoctor"))
                .andExpect(model().attributeExists("doctor"))
                .andExpect(model().attribute("doctor", doctorDto));
    }

    private static DoctorDto getDoctorDto(DoctorEntity doctorEntity) {
        return DoctorDto.builder()
                .doctorId(doctorEntity.getDoctorId())
                .specialization(doctorEntity.getSpecialization())
                .user(doctorEntity.getUser())
                .opinions(doctorEntity.getOpinions())
                .calendars(new TreeSet<>(doctorEntity.getCalendars()))
                .name(doctorEntity.getName())
                .email(doctorEntity.getEmail())
                .surname(doctorEntity.getSurname())
                .priceForVisit(doctorEntity.getPriceForVisit())
                .address(doctorEntity.getAddress())
                .calendars(new TreeSet<>(doctorEntity.getCalendars()))
                .appointments(doctorEntity.getAppointments())
                .build();
    }
}
