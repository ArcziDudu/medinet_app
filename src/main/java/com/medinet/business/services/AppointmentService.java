package com.medinet.business.services;

import com.medinet.api.controller.PdfDownloadController;
import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.AppointmentDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentDao appointmentDao;
    private final CalendarService calendarService;
    private final AppointmentMapper appointmentMapper;
    private final PdfGeneratorService pdfGeneratorService;
    private final PdfDownloadController pdfDownloadController;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm");

    public String getVisitNumber() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public OffsetDateTime issueInvoice() {
        return OffsetDateTime.now();
    }

    @Transactional
    public List<AppointmentDto> findAllAppointmentsByStatusAndDoctorID(String status, Integer doctorId) {
        return appointmentDao.findAllByStatus(status)
                .stream()
                .filter(a -> a.getDoctor().getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AppointmentDto> findAllAppointmentsByStatus(String status) {
        return appointmentDao.findAllByStatus(status);
    }

    @Transactional
    public AppointmentDto processAppointment(AppointmentEntity appointment) {
        {
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            if (appointment.getDateOfAppointment().isBefore(today)
                    || (appointment.getDateOfAppointment().equals(today)
                    && now.equals(appointment.getTimeOfVisit()))) {
                appointment.setStatus("pending");
            } else {
                appointment.setStatus("upcoming");
            }
            Optional<CalendarEntity> calendar = calendarService.findById(appointment.getCalendarId());
            calendar.orElseThrow().getHours().remove(appointment.getTimeOfVisit());

            appointmentDao.saveAppointment(appointment);
            return appointmentMapper.mapFromEntity(appointment);
        }

    }

    @Transactional
    public List<AppointmentDto> findUpcomingAppointments(PatientDto currentPatient) {
        return currentPatient.getAppointments().stream().filter(a -> a.getStatus().equals("upcoming"))
                .map(appointmentMapper::mapFromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AppointmentDto> findCompletedAppointments(PatientDto currentPatient) {
        return currentPatient.getAppointments().stream().filter(a -> a.getStatus().equals("done"))
                .map(appointmentMapper::mapFromEntity)
                .collect(Collectors.toList());

    }
    @Transactional
    public List<AppointmentDto> findPendingAppointments(PatientDto currentPatient) {
        return currentPatient.getAppointments().stream().filter(a -> a.getStatus().equals("pending"))
                .map(appointmentMapper::mapFromEntity)
                .collect(Collectors.toList());
    }
    @Transactional
    public void processRemovingAppointment(Integer appointmentID,
                                           LocalTime calendarHour,
                                           Integer calendarId) {
        Optional<CalendarEntity> calendar = calendarService.findById(calendarId);
        List<LocalTime> hours = calendar.orElseThrow().getHours();
        hours.add(calendarHour);
        hours.sort(new Comparator<LocalTime>() {
            @Override
            public int compare(LocalTime hour1, LocalTime hour2) {
                return hour1.compareTo(hour2);
            }
        });
        appointmentDao.removeAppointment(appointmentID);
    }

    @Transactional
    public void approveAppointment(Integer appointmentID, String message) {
        Optional<AppointmentEntity> optionalAppointment = appointmentDao.findById(appointmentID);
        if (optionalAppointment.isPresent()) {
            AppointmentEntity appointment = optionalAppointment.get();
            appointment.setStatus("done");
            appointment.setNoteOfAppointment(message);
        } else {
            throw new NotFoundException("not found");
        }
    }

    public AppointmentEntity findById(Integer appointmentId) {

        Optional<AppointmentEntity> appointmentById = appointmentDao.findById(appointmentId);
        if (appointmentById.isEmpty()) {
            throw new NotFoundException("Could not find appointment by Id: [%s]".formatted(appointmentId));
        }
        return appointmentById.get();
    }

    @Transactional
    public void save(AppointmentEntity appointment) {
        appointmentDao.saveAppointment(appointment);
    }

    @Transactional
    public void generatePdf(AppointmentEntity invoice) {
        String uuid = invoice.getUUID();
        OffsetDateTime nowDate = OffsetDateTime.now();
        pdfGeneratorService.generatePdf(generateHtmlFromInvoice(appointmentMapper.mapFromEntity(invoice), nowDate), uuid);
    }


    private String generateHtmlFromInvoice(AppointmentDto invoice, OffsetDateTime nowDate) {
        return
                "<!doctype html>" +
                        "<html>" +
                        "<head>" +
                        "<meta charset=UTF-8>" +
                        "<style>" +
                        "   body {" +
                        "position: relative;" +
                        "font-family: Arial, sans-serif;" +
                        "display: flex;" +
                        "margin-top: 50px;" +
                        "justify-content: center;" +
                        "align-items: flex-start;" +
                        "text-align: start;" +
                        "font-size: 19px; " +
                        "font-weight: bold; " +
                        "}" +
                        "h6{" +
                        "margin-top:30px" +
                        "}" +
                        "</style>" +

                        "</head>" +
                        "<body>" +
                        "<h4>Faktura: Wizyta w przychodni medinet</h4>" +
                        "<h4>Numer wizyty: " + invoice.getUUID() +"</h4>" +
                        "<p>Data wizyty: " + invoice.getDateOfAppointment() +"</p>" +
                        "<p>Godzina: " + invoice.getTimeOfVisit() +"</p>" +
                        "<p>Imie i nazwisko pacjenta: " + invoice.getPatient().getName() +" " + invoice.getPatient().getSurname() + "</p>" +
                        "<p>Adres przychodni: " + invoice.getDoctor().getAddress().getCity() + " " + invoice.getDoctor().getAddress().getStreet() + "</p>" +
                        "<p>Lekarz: " + invoice.getDoctor().getName() +" " + invoice.getDoctor().getSurname() + "</p>" +
                        "<p>Wizyta u specjalisty - " + invoice.getDoctor().getSpecialization() + "</p>" +
                        "<p>Informacje od lekarza: " + invoice.getNoteOfAppointment() + "</p>" +
                        "<p>Koszt wizyty: " + invoice.getDoctor().getPriceForVisit()+" zł</p>" +

                        "<h6>Wystawiono dnia: " + nowDate.format(formatter) + "</h6>" +
                        "</body>" +
                        "</html>";
    }

    public AppointmentEntity findByDateOfAppointmentAndTimeOfVisit(LocalDate dateOfAppointment, LocalTime timeOfVisit) {
        Optional<AppointmentEntity> appointment =
                appointmentDao.findByDateOfAppointmentAndTimeOfVisit(dateOfAppointment, timeOfVisit);
        return appointment.orElse(null);
    }

    public List<AppointmentDto> findAll() {
        return appointmentDao.findAll();
    }


}


