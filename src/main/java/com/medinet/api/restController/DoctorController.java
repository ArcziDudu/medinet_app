package com.medinet.api.restController;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(DoctorController.API_DOCTOR)
public class DoctorController {
    public static final String API_DOCTOR = "/api/doctor";
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @GetMapping(value = "/all")
    public ResponseEntity<List<DoctorDto>> carHistory(
    ) {
        List<DoctorDto> allDoctors = doctorService.findAllDoctors();
        return ResponseEntity
                .ok(allDoctors);
    }
}
