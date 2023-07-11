package com.medinet.api.controller;

import com.medinet.api.dto.CalendarDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.CalendarService;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.DoctorRepository;
import com.medinet.infrastructure.repository.mapper.CalendarMapper;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
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
    private final CalendarMapper calendarMapper;


    private final DateTimeFormatter polishMonthFormatter
            = DateTimeFormatter.ofPattern("LLL", new Locale("pl"));
    private final DateTimeFormatter polishDayFormatter
            = DateTimeFormatter.ofPattern("EEE", new Locale("pl"));
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm");

    private Map<String, ?> prepareNecessaryDataForDoctor(Principal principal) {

        String email = principal.getName();
        DoctorDto byEmail = doctorService.findByEmail(email);
        Integer doctorId = byEmail.getDoctorId();


        var completedAppointment = appointmentService.findAllCompletedAppointments("done",doctorId );
        var pendingAppointment = appointmentService.findAllCompletedAppointments("pending", doctorId);
        var upcomingAppointment = appointmentService.findAllCompletedAppointments("upcoming", doctorId);
        DoctorDto doctorById = doctorService.findDoctorById(doctorId);
        return Map.of(
                "doctor", doctorById,
                "format", formatter,
                "completedAppointment", completedAppointment,
                "pendingAppointment", pendingAppointment,
                "upcomingAppointment", upcomingAppointment
        );
    }


    @GetMapping(value = "/doctor")
    public ModelAndView doctorMainPage(Principal principal) {
        Map<String, ?> data = prepareNecessaryDataForDoctor(principal);
        return new ModelAndView("DoctorUpcomingAppointments", data);
    }

    @GetMapping(value = "/doctor/appointments/pending")
    public ModelAndView doctorPendingAppointmentsPage(Principal principal) {
        Map<String, ?> data = prepareNecessaryDataForDoctor(principal);
        return new ModelAndView("DoctorPendingAppointments", data);
    }

    @GetMapping(value = "/doctor/appointments/done")
    public ModelAndView doctorDoneAppointmentsPage(Principal principal) {
        Map<String, ?> data = prepareNecessaryDataForDoctor(principal);
        return new ModelAndView("DoctorDoneAppointments", data);
    }

    @GetMapping("/specialist/details/{doctorId}")
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
    public String getUser(@PathVariable("userId") Integer userId, Model model, Principal principal) {

        String email = principal.getName();
        DoctorDto byEmail = doctorService.findByEmail(email);
        Integer doctorId = byEmail.getDoctorId();

        DoctorDto user = doctorService.findDoctorById(userId);
        Set<CalendarEntity> calendars = doctorService.findDoctorById(doctorId).getCalendars();
        calendars.forEach(calendarMapper::mapFromEntity);

        List<LocalDate> dates = calendars.stream().map(CalendarEntity::getDate).toList();

        model.addAttribute("doctor", user);
        model.addAttribute("dates", dates);

        return "myAccountDoctor";
    }


}
