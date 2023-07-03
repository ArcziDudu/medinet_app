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


    //Set to sort by doctor's specializations and cities


    @GetMapping("/doctors")

    public String showUsersPage(@RequestParam(defaultValue = "0") int page, Model model) {

      Set<String> allAvailableCities = doctorService.findAllAvailableCities();
      Set<String> availableSpecialization = doctorService.findAllAvailableSpecialization();
        Page<DoctorDto> allDoctorsOnPage = doctorService.findAllDoctors(page);
        List<DoctorDto> allDoctorsInDatabase = doctorService.findAllDoctors();

        model.addAttribute("doctors", allDoctorsOnPage);
        model.addAttribute("doctorsInDatabase", allDoctorsInDatabase);
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
            Model model)
    {
         Set<String> allAvailableCities = doctorService.findAllAvailableCities();
        Set<String> availableSpecialization = doctorService.findAllAvailableSpecialization();

        List<DoctorDto> allDoctors = doctorService.findAllDoctorsBySpecializationAndCity(doctorSpecialization, doctorCity);

        model.addAttribute("doctors", allDoctors);
        model.addAttribute("specializations", availableSpecialization);
        model.addAttribute("cities", allAvailableCities);

        model.addAttribute("dateFormatter", polishMonthFormatter);
        model.addAttribute("polishDayFormatter", polishDayFormatter);

        return "doctors";
    }
    @GetMapping("/details/doctor")
    public String showSortedDoctorsPage(
            @RequestParam(value = "doctorId") Integer doctorId,
            Model model)
    {
        DoctorDto doctorProfile = doctorService.findDoctorById(doctorId);

        model.addAttribute("doctor", doctorProfile);
        model.addAttribute("dateFormatter", polishMonthFormatter);
        model.addAttribute("polishDayFormatter", polishDayFormatter);
        return "doctorProfile";
    }
}
