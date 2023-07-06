package com.medinet.api.controller;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.AppointmentEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientService patientService;
    private AppointmentService appointmentService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm");
    @GetMapping("/account/user")
    public String showUsersPage(Model model) {
        PatientDto currentPatient = patientService.findById(1);
        List<AppointmentDto> UpcomingAppointments = appointmentService.findUpcomingAppointments(currentPatient);
        List<AppointmentDto> completedAppointments = appointmentService.findCompletedAppointments(currentPatient);

        model.addAttribute("CurrentPatient", currentPatient);
        model.addAttribute("format", formatter);
        model.addAttribute("UpcomingAppointments", UpcomingAppointments);
        model.addAttribute("CompletedAppointments", completedAppointments);
        return "myAccount";
    }
}
