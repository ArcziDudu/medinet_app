package com.medinet.infrastructure.configuration;

import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.jpa.CalendarJpaRepository;
import com.medinet.infrastructure.repository.jpa.DoctorJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class BootstrapApplicationComponent implements ApplicationListener<ContextRefreshedEvent> {
    private DoctorJpaRepository doctorJpaRepository;
    private CalendarJpaRepository calendarJpaRepository;


    public static List<LocalDate> generateDateList() {
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusWeeks(2);

        List<LocalDate> dateTimeList = new ArrayList<>();

        while (currentDate.isBefore(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dateTimeList.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        return dateTimeList;
    }


    public static List<LocalTime> hoursArrayGenerator() {
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(16, 0);

        List<LocalTime> hours = new ArrayList<>();
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            hours.add(currentTime);
            currentTime = currentTime.plusHours(1);
        }
        return hours;
    }

    @Override
    @Transactional
    public void onApplicationEvent(final @NonNull ContextRefreshedEvent event) {
        if (calendarJpaRepository.findAll().size() > 0) {
            return;
        }

        List<LocalDate> twoWeeksDatesForDoctors = generateDateList();
        List<LocalTime> hours = hoursArrayGenerator();
        List<DoctorEntity> all = doctorJpaRepository.findAll();
        for (LocalDate date : twoWeeksDatesForDoctors) {
            for (DoctorEntity doctor : all) {
                CalendarEntity calendar = new CalendarEntity();
                calendar.setDoctor(doctor);
                calendar.setDate(date);
                calendar.setHours(hours);

                calendarJpaRepository.save(calendar);
            }
        }
    }
}
