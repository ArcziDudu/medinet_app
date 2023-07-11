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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.OffsetDateTime;

@Controller
@AllArgsConstructor
public class OpinionController {
    private OpinionService opinionService;
    private PatientService patientService;
    private DoctorService doctorService;
    private PatientMapper patientMapper;
    private DoctorMapper doctorMapper;
    private OpinionMapper opinionMapper;

    @PostMapping("/opinion/send")
    public String sendOpinion(
            @RequestParam("doctorId") Integer doctorId,
            @RequestParam("opinionNote") String note,
            Principal principal

    ) {
        String email = principal.getName();

        PatientDto patient = patientService.findByEmail(email);
        DoctorDto doctor = doctorService.findDoctorById(doctorId);

        OpinionDto opinion = new OpinionDto();
        opinion.setPatient(patientMapper.mapFromDto(patient));
        opinion.setDoctor(doctorMapper.mapFromDto(doctor));
        opinion.setNote(note);
        opinion.setDateOfCreateOpinion(OffsetDateTime.now());
        opinionService.processOpinion(opinionMapper.mapFromDto(opinion));
        return "redirect:/booking";
    }
}
