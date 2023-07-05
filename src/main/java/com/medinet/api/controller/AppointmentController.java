package com.medinet.api.controller;

import com.medinet.api.dto.AppointmentDto;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
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
    public String bookingAppointment(@RequestParam("doctorId") Integer doctorId,
                                  @RequestParam("patientId") Integer patientId,
                                  @RequestParam("selectedHour") String timeOfVisit,
                                  @RequestParam("selectedDate") LocalDate dateOfAppointment,
                                  @RequestParam("calendarId") Integer calendarId,
                                  Model model) {

        DoctorDto doctor = doctorService.findDoctorById(doctorId);
        PatientDto patient = patientService.findById(patientId);
        String UUID = appointmentService.getVisitNumber();


        model.addAttribute("doctor", doctor);
        model.addAttribute("calendarId", calendarId);
        model.addAttribute("patient", patient);
        model.addAttribute("selectedHour", timeOfVisit);
        model.addAttribute("selectedDate", dateOfAppointment);
        model.addAttribute("visitNumber", UUID);
        return "appointment";
    }

    @PostMapping("/booking/appointment")
    public String sendRequestToQueue(
            @ModelAttribute("DoctorDto") DoctorDto doctorDto,
            @ModelAttribute("PatientDto") PatientDto patientDto,
            @RequestParam("DateOfAppointment") LocalDate dateOfAppointment,
            @RequestParam("HourOfAppointment") String timeOfVisit,
            @RequestParam("UUID") String UUID,
            @RequestParam("calendarId") Integer calendarId
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
        appointment.setCalendarId(calendarId);
        appointment.setStatus("pending");
       appointmentService.processAppointment(appointment);

        return "redirect:/doctors";
    }

    @PostMapping("/appointment/approve/{appointmentId}")
    public String  approveAppointment( @PathVariable(value = "appointmentId") Integer appointmentID,
                                       @RequestParam("message") String message){
      appointmentService.approveAppointment(appointmentID, message);

        return "redirect:/doctor";
    }
    @DeleteMapping("booking/remove/{appointmentId}")
    public String removeAppointment(
            @PathVariable(value = "appointmentId") Integer appointmentID,
            @RequestParam(value = "selectedHour") String  calendarHour,
            @RequestParam(value = "calendarId") Integer  calendarId,
            Model model){

        appointmentService.processRemovingAppointment(appointmentID,calendarHour, calendarId);
        PatientDto currentPatient = patientService.findById(1);
        List<AppointmentDto> UpcomingAppointments = appointmentService.findUpcomingAppointments(currentPatient);
        List<AppointmentDto> completedAppointments = appointmentService.findCompletedAppointments(currentPatient);

        model.addAttribute("CurrentPatient", currentPatient);
        model.addAttribute("calendarId", calendarId);
        model.addAttribute("UpcomingAppointments", UpcomingAppointments);
        model.addAttribute("CompletedAppointments", completedAppointments);
        return "redirect:/account/user";
    }

}
