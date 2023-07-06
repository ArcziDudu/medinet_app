package com.medinet.api.controller;

import com.medinet.api.dto.CalendarDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.CalendarService;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.repository.mapper.CalendarMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class DoctorController {
    private DoctorService doctorService;
    private AppointmentService appointmentService;
    private final CalendarService calendarService;
    private final CalendarMapper calendarMapper;

    private final DateTimeFormatter polishMonthFormatter
            = DateTimeFormatter.ofPattern("LLL", new Locale("pl"));
    private final DateTimeFormatter polishDayFormatter
            = DateTimeFormatter.ofPattern("EEE", new Locale("pl"));
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm");

    private Map<String, ?> prepareNecessaryDataForDoctor() {
        var completedAppointment = appointmentService.findAllCompletedAppointments("done");
        var pendingAppointment = appointmentService.findAllCompletedAppointments("pending");
        var upcomingAppointment = appointmentService.findAllCompletedAppointments("upcoming");
        DoctorDto doctorById = doctorService.findDoctorById(1);
        return Map.of(
                "doctor", doctorById,
                "format", formatter,
                "completedAppointment", completedAppointment,
                "pendingAppointment", pendingAppointment,
                "upcomingAppointment", upcomingAppointment
        );
    }



    @GetMapping(value = "/doctor")
    public ModelAndView doctorMainPage() {
        Map<String, ?> data = prepareNecessaryDataForDoctor();
        return new ModelAndView("DoctorUpcomingAppointments", data);
    }
    @GetMapping(value = "/doctor/appointments/pending")
    public ModelAndView doctorPendingAppointmentsPage() {
        Map<String, ?> data = prepareNecessaryDataForDoctor();
        return new ModelAndView("DoctorPendingAppointments", data);
    }
    @GetMapping(value = "/doctor/appointments/done")
    public ModelAndView doctorDoneAppointmentsPage() {
        Map<String, ?> data = prepareNecessaryDataForDoctor();
        return new ModelAndView("DoctorDoneAppointments", data);
    }

    @GetMapping("/booking")
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

        return "mainPageBookingAppointments";
    }

    @GetMapping("/booking/find")
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

        return "mainPageBookingAppointments";
    }

    @GetMapping("/doctor/details/{doctorId}")
    public String showSortedDoctorsPage(
            @PathVariable(value = "doctorId") Integer doctorId,
            Model model) {
        DoctorDto doctorProfile = doctorService.findDoctorById(doctorId);

        model.addAttribute("doctor", doctorProfile);
        model.addAttribute("dateFormatter", polishMonthFormatter);
        model.addAttribute("polishDayFormatter", polishDayFormatter);
        return "doctorDetails";
    }

    @GetMapping("/user/{userId}")
    public String getUser(@PathVariable("userId") Integer userId, Model model) {

        DoctorDto user = doctorService.findDoctorById(userId);
        Set<CalendarEntity> calendars = doctorService.findDoctorById(1).getCalendars();
        calendars.forEach(calendarMapper::mapFromEntity);

        List<LocalDate> dates = calendars.stream().map(CalendarEntity::getDate).toList();

        model.addAttribute("doctor", user);
        model.addAttribute("dates", dates);

        return "myAccountDoctor";
    }


}
