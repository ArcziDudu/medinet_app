package com.medinet.business.services;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.CalendarDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.AppointmentDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import com.medinet.infrastructure.repository.mapper.CalendarMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AppointmentService {
    private final AppointmentDao appointmentDao;
    private final CalendarService calendarService;
    private final CalendarMapper calendarMapper;
    private final AppointmentMapper appointmentMapper;
    private final PdfGeneratorService pdfGeneratorService;


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
            CalendarDto calendar = calendarService.findById(appointment.getCalendarId());
            calendar.getHours().remove(appointment.getTimeOfVisit());

            calendarService.save(calendarMapper.mapFromDto(calendar));
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
        CalendarDto calendarById = calendarService.findById(calendarId);
        List<LocalTime> hours = calendarById.getHours();
        hours.add(calendarHour);
        hours.sort(new Comparator<LocalTime>() {
            @Override
            public int compare(LocalTime hour1, LocalTime hour2) {
                return hour1.compareTo(hour2);
            }
        });
        calendarService.save(calendarMapper.mapFromDto(calendarById));
        appointmentDao.removeAppointment(appointmentID);
    }

    @Transactional
    public void approveAppointment(Integer appointmentID, String message) {
        Optional<AppointmentDto> appointmentById = appointmentDao.findById(appointmentID);
        if (appointmentById.isPresent() && appointmentById.get().getStatus().equals("pending")) {
            AppointmentDto appointmentDto = appointmentById.get();
            appointmentDto.setStatus("done");
            appointmentDto.setNoteOfAppointment(message);
            appointmentDao.saveAppointment(appointmentMapper.mapFromDto(appointmentById.get()));
        } else {
            throw new NotFoundException("Could not find appointment by Id: [%s]".formatted(appointmentID));
        }
    }

    public AppointmentDto findById(Integer appointmentId) {

        Optional<AppointmentDto> appointmentById = appointmentDao.findById(appointmentId);
        if (appointmentById.isEmpty()) {
            throw new NotFoundException("Could not find appointment by Id: [%s]".formatted(appointmentId));
        }
        return appointmentById.get();
    }

    @Transactional
    public void save(AppointmentEntity appointment) {
        appointmentDao.saveAppointment(appointment);
    }

    public void generatePdf(AppointmentDto invoice) {
        String uuid = invoice.getUUID();
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        OffsetDateTime nowDate = OffsetDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        pdfGeneratorService.generatePdf("<!doctype html><html><head><meta charset=utf-8><style>.invoice-box{max-width:800px;padding:30px;border:1px solid #eee;box-shadow:0 0 10px rgba(0,0,0,.15);font-size:16px;line-height:24px;font-family:'Helvetica Neue','Helvetica',Helvetica,Arial,sans-serif;color:#555;}.invoice-box table{width:100%;line-height:inherit;text-align:left;}.invoice-box table td{padding:5px;vertical-align:top;}.invoice-box table tr td:nth-child(2){text-align:right;}.invoice-box table tr.top table td{padding-bottom:20px;}.invoice-box table tr.top table td.title{font-size:45px;line-height:45px;color:#333;}.invoice-box table tr.information table td{padding-bottom:40px;}.invoice-box table tr.heading td{background:#eee;border-bottom:1px solid #ddd;font-weight:bold;}.invoice-box table tr.details td{padding-bottom:20px;}.invoice-box table tr.item td{border-bottom:1px solid #eee;}.invoice-box table tr.item.last td{border-bottom:none;}.invoice-box table tr.total td:nth-child(2){border-top:2px solid #eee;font-weight:bold;}@media only screen and (max-width:600px){.invoice-box table tr.top table td{width:100%;display:block;text-align:center;}.invoice-box table tr.information table td{width:100%;display:block;text-align:center;}}/** RTL **/.rtl{direction:rtl;font-family:Tahoma,'Helvetica Neue','Helvetica',Helvetica,Arial,sans-serif;}.rtl table{text-align:right;}.rtl table tr td:nth-child(2){text-align:left;}</style></head><body><div class=invoice-box><table cellpadding=0 cellspacing=0><tr class=top><td colspan=2><table><tr><td class=title><h6 style=color:blue>Medinet</h6></td><td>Faktura: "
                        + invoice.getUUID() + "<br>Data wizyty: "
                        + invoice.getDateOfAppointment() + "<br>Godzina wizyty: "
                        + invoice.getTimeOfVisit() + "<br>Adres placówki: "
                        + invoice.getDoctor().getAddress().getCity() + " "
                        + invoice.getDoctor().getAddress().getStreet() + "</td></tr></table></td></tr><tr class=heading><td>Tytuł faktury</td><td>wizyta w przychodni Medinet</td></tr><tr class=heading><td>Usługa</td><td>wizyta u specjalisty - "
                        + invoice.getDoctor().getSpecialization() + "</td></tr><tr class=item><td>Lekarz</td><td>"
                        + invoice.getDoctor().getName() + " " + invoice.getDoctor().getSurname() + "</td></tr><tr class=item><td>Pacjent</td><td>"
                        + invoice.getPatient().getName() + " " + invoice.getPatient().getSurname() + "</td></tr><tr class=item><td>Czas trwania wizyty</td><td>60 minut</td></tr><td>Metoda płatności</td><td>Przelew</td><tr class=total><td></td><td>Koszt: "
                        + invoice.getDoctor().getPriceForVisit() + " zł.</td></tr><tr class=item><td>Data wystawienia faktury</td><td>"
                        + nowDate.format(formatter) + "</td></tr><tr class=item><td><p style=font-weight:bold>Informacja od lekarza</p><span>"
                        + invoice.getNoteOfAppointment() + "</span></td></tr></table></div></body></html>"
                , uuid);
    }


    public List<AppointmentDto> findAll() {
        return appointmentDao.findAll();
    }


    public boolean existByDateAndTimeOfVisit(LocalDate dateOfAppointment, LocalTime timeOfVisit) {
        return appointmentDao.existByDateAndTimeOfVisit(dateOfAppointment, timeOfVisit);
    }
}


