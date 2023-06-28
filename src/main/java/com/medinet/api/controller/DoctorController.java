package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class DoctorController {
    private DoctorService doctorService;

    @GetMapping("/doctors")
    public String showUsersPage(Model model) {
        List<DoctorDto> allDoctors = doctorService.findAllDoctors();
        model.addAttribute("doctors", allDoctors);
        return "doctors";
    }


}
