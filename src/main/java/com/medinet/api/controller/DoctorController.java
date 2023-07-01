package com.medinet.api.controller;

import com.medinet.api.dto.CalendarDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.CalendarService;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Controller
@AllArgsConstructor
public class DoctorController {
    private DoctorService doctorService;
    private CalendarService calendarService;

    private final DateTimeFormatter polishMonthFormatter = DateTimeFormatter.ofPattern("LLL", new Locale("pl"));
    private final DateTimeFormatter polishDayFormatter = DateTimeFormatter.ofPattern("EEE", new Locale("pl"));



    @GetMapping("/doctors/all")
    
    public String showUsersPage(Model model) {
        List<DoctorDto> allDoctors = doctorService.findAllDoctors();
        List<CalendarDto> allCalendars = calendarService.findAllCalendar();
        Set<String> availableSpecialization = doctorService.findAllAvailableSpecialization();

        model.addAttribute("doctors", allDoctors);
        model.addAttribute("calendars", allCalendars);

        model.addAttribute("specializations", availableSpecialization);
        model.addAttribute("dateFormatter", polishMonthFormatter);
        model.addAttribute("polishDayFormatter", polishDayFormatter);


        return "doctors";
    }
    @GetMapping("/doctors/all/find")
    public String showSortedDoctorsPage(
            @RequestParam(value = "doctorSpecialization")
            String doctorSpecialization,
            Model model) {

        List<DoctorDto> allDoctors = doctorService.findAllDoctorsSortedBySpecializationAndCity(doctorSpecialization);
        List<CalendarDto> allCalendars = calendarService.findAllCalendar();
        Set<String> availableSpecialization = doctorService.findAllAvailableSpecialization();


        model.addAttribute("doctors", allDoctors);
        model.addAttribute("calendars", allCalendars);
        model.addAttribute("specializations", availableSpecialization);


        model.addAttribute("dateFormatter", polishMonthFormatter);
        model.addAttribute("polishDayFormatter", polishDayFormatter);

        return "doctors";
    }

}
