package com.medinet.api.controller;

import com.medinet.api.dto.OpinionDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;

@Controller
@AllArgsConstructor
public class AppointmentController {
    private AppointmentService appointmentService;
//    @PostMapping("/booking/visit")
//    public String showUsersPage(
//            @ModelAttribute("OpinionDto") OpinionDto opinionDto,
//            @RequestParam("doctorId") Integer doctorId,
//            @RequestParam("opinionNote") String note
//
//    ) {
//        Integer patientId = 1;
//
//        // Pobierz pacjenta i lekarza na podstawie ich identyfikatorów
//        PatientEntity patient = patientMapper.mapFromDto(patientService.findById(patientId));
//        DoctorEntity doctor = doctorMapper.mapFromDto(doctorService.findDoctorById(doctorId));
//
//        // Utwórz obiekt Opinion i ustaw niezbędne właściwości
//        OpinionEntity opinion = new OpinionEntity();
//        opinion.setPatient(patient);
//        opinion.setDoctor(doctor);
//        opinion.setNote(note);
//        opinion.setDateOfCreateOpinion(OffsetDateTime.now()); // Ustaw aktualną datę
//        opinionService.processOpinion(opinion);
//        return "redirect:/doctors";
//    }
}
