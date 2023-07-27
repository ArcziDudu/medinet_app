package com.medinet.api.controller.rest;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping(DoctorRestController.API_DOCTOR)
public class DoctorRestController {
    private DoctorMapper doctorMapper;
    public static final String API_DOCTOR = "/api/doctor";
    public static final String API_ALL_DOCTOR = "/all";
    public static final String API_ALL_DOCTOR_PAGE = "/all/{page}";
    public static final String API_ONE_DOCTOR = "/{doctorId}";
    public static final String API_CREATE_DOCTOR = "/create";
    public static final String DOCTOR_ID_RESULT = "/%s";
    private final DoctorService doctorService;


    @GetMapping(value = API_ONE_DOCTOR)
    @Operation(summary = "Get one doctor by ID", description = "Retrieve information about a doctor based on their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<DoctorDto> oneDoctorById(@PathVariable Integer doctorId) {
        DoctorDto doctorDto = doctorService.findDoctorById(doctorId);
        if (Objects.isNull(doctorDto)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctorDto);
    }

    @GetMapping(value = API_ALL_DOCTOR)
    @Operation(summary = "Get available doctors", description = "Retrieve list of available doctors")
    public List<DoctorDto> allDoctors() {
        return doctorService.findAllDoctors();
    }

    @Operation(summary = "Get one page with available doctors")
    @GetMapping(API_ALL_DOCTOR_PAGE)
    public ResponseEntity<Page<DoctorDto>> getAllDoctorsByPage(
            @PathVariable Integer page) {
        Page<DoctorDto> doctorsPage = doctorService.findAllDoctors(page);
        return ResponseEntity.ok(doctorsPage);
    }

    @Operation(summary = "Create a new doctor", description = "Create a new doctor based on the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping(value = API_CREATE_DOCTOR, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DoctorDto> createDoctor(@RequestBody DoctorDto doctorDto) {

        DoctorEntity created = doctorService.create(doctorMapper.mapFromDto(doctorDto));
        return ResponseEntity
                .created(URI.create(API_CREATE_DOCTOR + DOCTOR_ID_RESULT.formatted(created.getDoctorId())))
                .build();
    }

    @Operation(summary = "Delete a doctor by ID", description = "Delete a doctor based on their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor deleted"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @DeleteMapping(value = API_ONE_DOCTOR)
    public ResponseEntity<?> deleteDoctor(@PathVariable Integer doctorId) {
        doctorService.deleteById(doctorId);
        return ResponseEntity.ok().build();
    }

}
