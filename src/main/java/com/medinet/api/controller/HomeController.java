package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class HomeController {
    static final String HOME = "/";
    static final String POLICY = "/policy";
    static final String BOOKING = "/booking";
    static final String BOOKING_FIND = "/booking/find";
    static final String TRY_LATER = "/error/invoice";

    private DoctorService doctorService;
    private final UserRepository userRepository;
    private final DateTimeFormatter polishMonthFormatter
            = DateTimeFormatter.ofPattern("LLL", new Locale("pl"));
    private final DateTimeFormatter polishDayFormatter
            = DateTimeFormatter.ofPattern("EEE", new Locale("pl"));

    @RequestMapping(value = HOME, method = RequestMethod.GET)
    public String homepage() {
        return "home";
    }
    @RequestMapping(value = TRY_LATER, method = RequestMethod.GET)
    public String tryLater() {
        return "internalServerErrorFromApi";
    }

    @RequestMapping(value = POLICY, method = RequestMethod.GET)
    public String policy() {
        return "policy";
    }


    @GetMapping(BOOKING)
    public String showBookingPage(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasAccessPatient = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("PATIENT"));


        boolean hasAccessDoctor = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("DOCTOR"));

        if (hasAccessPatient) {
            String email = principal.getName();
            UserEntity currentUser = userRepository.findByEmail(email);
            int id = currentUser.getId();

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
            model.addAttribute("user", id);
            return "mainPageBookingAppointments";

        } else if (hasAccessDoctor) {
            return "redirect:/doctor";
        }

        return "redirect:/";
    }


    @GetMapping(BOOKING_FIND)
    public String showSortedDoctorsPage(
            @RequestParam(value = "doctorSpecialization") String doctorSpecialization,
            @RequestParam(value = "doctorCity") String doctorCity,
            @RequestParam(defaultValue = "0") int page,
            Principal principal,
            Model model) {

        String email = principal.getName();
        UserEntity currentUser = userRepository.findByEmail(email);
        int id = currentUser.getId();

        Set<String> allAvailableCities = doctorService.findAllAvailableCities();
        Set<String> availableSpecialization = doctorService.findAllAvailableSpecialization();

        Page<DoctorDto> allDoctors = doctorService.findAllDoctorsBySpecializationAndCity(
                doctorSpecialization,
                doctorCity,
                page);
        if (allDoctors.getSize() > 0) {
            for (DoctorDto doctor : allDoctors) {
                TreeSet<CalendarEntity> sortedCalendars = doctor.getCalendars().stream()
                        .filter(Objects::nonNull)
                        .sorted(getCalendarEntityComparator())
                        .collect(Collectors.toCollection(TreeSet::new));
                doctor.setCalendars(sortedCalendars);
            }
        }

        long totalElements = doctorService.findAllDoctorsBySpecializationAndCity(
                        doctorSpecialization,
                        doctorCity,
                        page)
                .getTotalElements();

        List<DoctorDto> allDoctorsInDatabase = doctorService.findAllDoctors();

        model.addAttribute("totalElements", totalElements);
        model.addAttribute("doctors", allDoctors);
        model.addAttribute("doctorsInDatabase", allDoctorsInDatabase);
        model.addAttribute("specializations", availableSpecialization);
        model.addAttribute("cities", allAvailableCities);
        model.addAttribute("dateFormatter", polishMonthFormatter);
        model.addAttribute("polishDayFormatter", polishDayFormatter);
        model.addAttribute("user", id);


        return "mainPageBookingAppointments";
    }

    public static Comparator<CalendarEntity> getCalendarEntityComparator() {
        return Comparator.comparing(CalendarEntity::getDate);
    }
}
