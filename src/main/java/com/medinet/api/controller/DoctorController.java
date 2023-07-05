package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.CalendarService;
import com.medinet.business.services.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Controller
@AllArgsConstructor
public class DoctorController {
    private DoctorService doctorService;


    //DateTimeFormatter for rendered calendars in doctors' cards
    private final DateTimeFormatter polishMonthFormatter
            = DateTimeFormatter.ofPattern("LLL", new Locale("pl"));
    private final DateTimeFormatter polishDayFormatter
            = DateTimeFormatter.ofPattern("EEE", new Locale("pl"));

    @GetMapping("/doctors")
    public String showUsersPage(@RequestParam(defaultValue = "0") int page, Model model) {

        Set<String> allAvailableCities = doctorService.findAllAvailableCities();
        Set<String> availableSpecialization = doctorService.findAllAvailableSpecialization();

        Page<DoctorDto> allDoctorsOnPage = doctorService.findAllDoctors(page);
        long totalElements = doctorService.findAllDoctors(page).getTotalElements();


        model.addAttribute("doctors", allDoctorsOnPage);
        model.addAttribute("totalElements", totalElements);

        model.addAttribute("specializations", availableSpecialization);
        model.addAttribute("cities", allAvailableCities);
        model.addAttribute("dateFormatter", polishMonthFormatter);
        model.addAttribute("polishDayFormatter", polishDayFormatter);

        return "doctors";
    }

    @GetMapping("/doctors/find")
    public String showSortedDoctorsPage(
            @RequestParam(value = "doctorSpecialization") String doctorSpecialization,
            @RequestParam(value = "doctorCity") String doctorCity,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Set<String> allAvailableCities = doctorService.findAllAvailableCities();
        Set<String> availableSpecialization = doctorService.findAllAvailableSpecialization();

        Page<DoctorDto> allDoctors = doctorService.findAllDoctorsBySpecializationAndCity(doctorSpecialization, doctorCity, page);
        List<DoctorDto> allDoctorsInDatabase = doctorService.findAllDoctors();

        long totalElements = doctorService.findAllDoctorsBySpecializationAndCity(doctorSpecialization, doctorCity, page).getTotalElements();

        model.addAttribute("totalElements", totalElements);
        model.addAttribute("doctors", allDoctors);
        model.addAttribute("doctorsInDatabase", allDoctorsInDatabase);
        model.addAttribute("specializations", availableSpecialization);
        model.addAttribute("cities", allAvailableCities);

        model.addAttribute("dateFormatter", polishMonthFormatter);
        model.addAttribute("polishDayFormatter", polishDayFormatter);

        return "doctors";
    }

    @GetMapping("/doctor/details/{doctorId}")
    public String showSortedDoctorsPage(
            @PathVariable(value = "doctorId") Integer doctorId,
            Model model) {
        DoctorDto doctorProfile = doctorService.findDoctorById(doctorId);

        model.addAttribute("doctor", doctorProfile);
        model.addAttribute("dateFormatter", polishMonthFormatter);
        model.addAttribute("polishDayFormatter", polishDayFormatter);
        return "doctorProfile";
    }
}
