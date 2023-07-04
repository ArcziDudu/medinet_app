package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.OpinionDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

@Controller
@AllArgsConstructor
public class AppointmentController {
    private AppointmentService appointmentService;
    private DoctorService doctorService;
    private PatientService patientService;

    @GetMapping("/booking/appointment")
    public String bookAppointment(@RequestParam("doctorId") Integer doctorId,
                                  @RequestParam("patientId") Integer patientId,
                                  @RequestParam("selectedHour") String selectedHour,
                                  @RequestParam("selectedDate") LocalDate selectedDate,
                                  Model model) {

        DoctorDto doctor = doctorService.findDoctorById(doctorId);
        PatientDto patient = patientService.findById(patientId);
        OffsetDateTime IssueDate = appointmentService.getIssueDate();
        String visitNumber = appointmentService.getVisitNumber();


        model.addAttribute("doctor", doctor);
        model.addAttribute("patient", patient);
        model.addAttribute("selectedHour", selectedHour);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("issueDate", IssueDate);
        model.addAttribute("visitNumber", visitNumber);
        System.out.println(visitNumber);
        System.out.println(IssueDate);

        return "appointment";
    }
}
