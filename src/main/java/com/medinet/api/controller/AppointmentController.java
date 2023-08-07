package com.medinet.api.controller;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@AllArgsConstructor
public class AppointmentController {
    private AppointmentService appointmentService;
    private DoctorService doctorService;
    private PatientService patientService;
    private DoctorMapper doctorMapper;
    private PatientMapper patientMapper;
    private final UserRepository userRepository;
    private final String REQUEST = "/request";
    private final String BOOKING_APPOINTMENT = "/booking/appointment";
    private final String APPOINTMENT_APPROVE_ID = "/appointment/approve/{appointmentId}";
    private final String APPOINTMENT_REMOVE_ID = "/booking/remove/{appointmentId}";
    private final String GENERATE_PDF = "/invoice/generatePdf/{appointmentId}";

    @GetMapping(REQUEST)
    public String bookingAppointment(@RequestParam("doctorId") Integer doctorId,
                                     @RequestParam("patientId") Integer patientId,
                                     @RequestParam("selectedHour") LocalTime timeOfVisit,
                                     @RequestParam("selectedDate") LocalDate dateOfAppointment,
                                     @RequestParam("calendarId") Integer calendarId,
                                     Model model) {

        DoctorDto doctor = doctorService.findDoctorById(doctorId);
        PatientDto patient = patientService.findByUserId(patientId);
        String UUID = appointmentService.getVisitNumber();


        model.addAttribute("doctor", doctor);
        model.addAttribute("calendarId", calendarId);
        model.addAttribute("patient", patient);
        model.addAttribute("selectedHour", timeOfVisit);
        model.addAttribute("selectedDate", dateOfAppointment);
        model.addAttribute("visitNumber", UUID);
        return "appointmentBooking";
    }

    @PostMapping(BOOKING_APPOINTMENT)
    public String sendRequestToQueue(
            @RequestParam("DateOfAppointment") LocalDate dateOfAppointment,
            @RequestParam("HourOfAppointment") LocalTime timeOfVisit,
            @RequestParam("UUID") String UUID,
            @RequestParam("calendarId") Integer calendarId,
            @RequestParam("doctorId") Integer doctorId,
            Principal principal
    ) {

        String email = principal.getName();
        UserEntity currentUser = userRepository.findByEmail(email);
        int id = currentUser.getId();

        try {
            DoctorDto doctor = doctorService.findDoctorById(doctorId);
            PatientDto patient = patientService.findByUserId(id);
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
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "redirect:/booking?success=true";
    }

    @PostMapping(APPOINTMENT_APPROVE_ID)
    public String approveAppointment(@PathVariable(value = "appointmentId") Integer appointmentID,
                                     @RequestParam("message") String message) {
        AppointmentDto appointmentById = appointmentService.findById(appointmentID);
        appointmentService.approveAppointment(appointmentID, message);
        appointmentService.generatePdf(appointmentById);
        return "redirect:/doctor?approve=true";
    }

    @DeleteMapping(APPOINTMENT_REMOVE_ID)
    public String removeAppointment(
            @PathVariable(value = "appointmentId") Integer appointmentID,
            @RequestParam(value = "selectedHour") LocalTime calendarHour,
            @RequestParam(value = "calendarId") Integer calendarId,
            Principal principal,
            Model model) {

        String email = principal.getName();
        UserEntity currentUser = userRepository.findByEmail(email);
        int id = currentUser.getId();

        appointmentService.processRemovingAppointment(appointmentID, calendarHour, calendarId);
        PatientDto currentPatient = patientService.findByUserId(id);
        List<AppointmentDto> UpcomingAppointments = appointmentService.findUpcomingAppointments(currentPatient);
        List<AppointmentDto> completedAppointments = appointmentService.findCompletedAppointments(currentPatient);

        model.addAttribute("CurrentPatient", currentPatient);
        model.addAttribute("calendarId", calendarId);
        model.addAttribute("UpcomingAppointments", UpcomingAppointments);
        model.addAttribute("CompletedAppointments", completedAppointments);
        return "redirect:/account/user/" + id + "?remove=true";

    }

    @PostMapping(GENERATE_PDF)
    public String generatePdf(
            @PathVariable("appointmentId") Integer appointmentId) {
        AppointmentDto appointmentById = appointmentService.findById(appointmentId);
        String uuid = appointmentById.getUUID();
        return "redirect:/api/invoice/download/" + uuid;
    }


}
