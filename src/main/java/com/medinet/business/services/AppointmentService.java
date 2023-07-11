package com.medinet.business.services;

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

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm");
    public String getVisitNumber() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public OffsetDateTime issueInvoice() {
        return OffsetDateTime.now();
    }

    public List<AppointmentDto> findAllCompletedAppointments(String status) {
        return appointmentDao.findAllByStatus(status);
    }


    @Transactional
    public void processAppointment(AppointmentEntity appointment) {
        {
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            if (appointment.getDateOfAppointment().isBefore(today)
                    || (appointment.getDateOfAppointment().equals(today)
                    && now.equals(LocalTime.parse(appointment.getTimeOfVisit(), formatter)))) {
                appointment.setStatus("pending");
            } else {
                appointment.setStatus("upcoming");
            }
            Optional<CalendarEntity> calendar = calendarService.findById(appointment.getCalendarId());
            calendar.orElseThrow().getHours().remove(appointment.getTimeOfVisit());

            appointmentDao.saveAppointment(appointment);
        }

    }


    public List<AppointmentDto> findUpcomingAppointments(PatientDto currentPatient) {
        return currentPatient.getAppointments().stream().filter(a -> a.getStatus().equals("upcoming"))
                .map(appointmentMapper::mapFromEntity)
                .collect(Collectors.toList());
    }

    public List<AppointmentDto> findCompletedAppointments(PatientDto currentPatient) {
        return currentPatient.getAppointments().stream().filter(a -> a.getStatus().equals("done"))
                .map(appointmentMapper::mapFromEntity)
                .collect(Collectors.toList());

    }

    @Transactional
    public void processRemovingAppointment(Integer appointmentID,
                                           String calendarHour,
                                           Integer calendarId) {
        Optional<CalendarEntity> calendar = calendarService.findById(calendarId);
        List<String> hours = calendar.orElseThrow().getHours();
        hours.add(calendarHour);
        hours.sort(new Comparator<String>() {
            @Override
            public int compare(String hour1, String hour2) {
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

    public Optional<AppointmentEntity> findById(Integer appointmentId) {
        return appointmentDao.findById(appointmentId);
    }

    public void save(AppointmentEntity appointment) {
        appointmentDao.saveAppointment(appointment);
    }

    public void generatePdf(Optional<AppointmentEntity> invoice) throws Exception {
        String uuid = invoice.get().getUUID();
        OffsetDateTime nowDate = OffsetDateTime.now();
        pdfGeneratorService.generatePdf(generateHtmlFromInvoice(appointmentMapper.mapFromEntity(invoice.get()), nowDate), uuid);
    }

        private String generateHtmlFromInvoice (AppointmentDto invoice, OffsetDateTime nowDate){
            return
                    "<!doctype html>" +
                    "<html>" +
                    "<head>" +
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
                            "margin-top:30px"+
                            "}"+
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<h4>Faktura: Wizyta w przychodni medinet</h4>" +
                    "<h4>Numer wizyty: " + invoice.getUUID() + "</h4>" +
                    "<p>Data wizyty: " + invoice.getDateOfAppointment() + "</p>" +
                    "<p>Godzina: " + invoice.getTimeOfVisit() + "</p>" +
                    "<p>Imie i nazwisko pacjenta: " + invoice.getPatient().getName() + " " + invoice.getPatient().getSurname() + "</p>" +
                    "<p>Adres przychodni: " + invoice.getDoctor().getAddress().getCity() + " " + invoice.getDoctor().getAddress().getStreet() + "</p>" +
                    "<p>Lekarz: " + invoice.getDoctor().getName() + " " + invoice.getDoctor().getSurname() + "</p>" +
                    "<p>Wizyta u specjalisty - " + invoice.getDoctor().getSpecialization() + "</p>" +
                    "<p>Informacje od lekarza: " + invoice.getNoteOfAppointment() + "</p>" +

                    "<h6>Wystawiono dnia: " +nowDate.format(formatter)+ "</h6>" +
                    "</body>" +
                    "</html>";
        }
    }


