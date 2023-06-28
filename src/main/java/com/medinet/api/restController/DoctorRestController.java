package com.medinet.api.restController;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(DoctorRestController.API_DOCTOR)
public class DoctorRestController {
    public static final String API_DOCTOR = "/api/doctor";
    public final String API_ALL_DOCTOR = "/all";
    public final String API_ALL_DOCTOR_BY_CITY = "/all/{city}";
    private final DoctorService doctorService;


    @GetMapping(value = API_ALL_DOCTOR)
    public ResponseEntity<List<DoctorDto>> allDoctors(
    ) {
        List<DoctorDto> allDoctors = doctorService.findAllDoctors();
        return ResponseEntity
                .ok(allDoctors);
    }
    @GetMapping(value = API_ALL_DOCTOR_BY_CITY)
    public ResponseEntity<List<DoctorDto>> allDoctorsByCity(
            @PathVariable String city
    ) {
        List<DoctorDto> allDoctors = doctorService.findAllDoctorsByCityWhereTheyWork(city);
        return ResponseEntity
                .ok(allDoctors);
    }
}
