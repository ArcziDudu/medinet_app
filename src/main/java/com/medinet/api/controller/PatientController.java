package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientService patientService;

    @GetMapping("/account/user")
    public String showUsersPage( Model model) {
        PatientDto currentPatient = patientService.findById(1);
        List<PatientDto> allPatients = patientService.findAllPatients();

        model.addAttribute("patients", allPatients);

        return "myAccount";
    }

}
