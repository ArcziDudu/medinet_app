package com.medinet.api.restController;

import com.medinet.api.dto.CalendarDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.CalendarService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(CalendarRestController.CALENDAR_API)
public class CalendarRestController {
       public static final String CALENDAR_API = "api/calendar";

       public final String API_ALL_CALENDARS = "/all";
       private final CalendarService calendarService;
        @GetMapping(value = API_ALL_CALENDARS)
        public ResponseEntity<List<CalendarDto>> allCalendars(
        ) {
                List<CalendarDto> allCalendars = calendarService.findAllCalendar();
                return ResponseEntity
                        .ok(allCalendars);
        }
}
