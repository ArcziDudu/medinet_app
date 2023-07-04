package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.OpinionDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
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
    private DoctorMapper doctorMapper;
    private PatientMapper patientMapper;

    @GetMapping("/request")
    public String bookAppointment(@RequestParam("doctorId") Integer doctorId,
                                  @RequestParam("patientId") Integer patientId,
                                  @RequestParam("selectedHour") String timeOfVisit,
                                  @RequestParam("selectedDate") LocalDate dateOfAppointment,
                                  Model model) {

        DoctorDto doctor = doctorService.findDoctorById(doctorId);
        PatientDto patient = patientService.findById(patientId);
        String UUID = appointmentService.getVisitNumber();


        model.addAttribute("doctor", doctor);
        model.addAttribute("patient", patient);
        model.addAttribute("selectedHour", timeOfVisit);
        model.addAttribute("selectedDate", dateOfAppointment);
        model.addAttribute("visitNumber", UUID);
        System.out.println(UUID);

        return "appointment";
    }

    @PostMapping("/booking/appointment")
    public String showUsersPage(
            @ModelAttribute("DoctorDto") DoctorDto doctorDto,
            @ModelAttribute("PatientDto") PatientDto patientDto,
            @RequestParam("DateOfAppointment") LocalDate dateOfAppointment,
            @RequestParam("HourOfAppointment") String timeOfVisit,
            @RequestParam("UUID") String UUID
    ) {
        DoctorDto doctor = doctorService.findDoctorById(1);
        PatientDto patient = patientService.findById(1);
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setDateOfAppointment(dateOfAppointment);
        appointment.setUUID(UUID);
        appointment.setDoctor(doctorMapper.mapFromDto(doctor));
        appointment.setPatient(patientMapper.mapFromDto(patient));
        appointment.setTimeOfVisit(timeOfVisit);
        appointment.setIssueInvoice(appointmentService.issueInvoice());
        System.out.println(appointmentService.issueInvoice());
        appointment.setStatus(true);
       appointmentService.processAppointment(appointment);


        return "redirect:/doctors";
    }
}
