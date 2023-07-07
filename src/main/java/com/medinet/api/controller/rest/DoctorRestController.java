package com.medinet.api.controller.rest;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import lombok.AllArgsConstructor;
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
    public final String API_ALL_DOCTOR = "/all";
    public final String API_ONE_DOCTOR = "/{doctorId}";
    public final String API_CREATE_DOCTOR = "/create";
    public static final String DOCTOR_ID_RESULT = "/%s";
    public final String API_AVAILABLE_HOURS_OF_WORK = "/hours/{doctorId}";
    private final DoctorService doctorService;



    @GetMapping(value = API_ONE_DOCTOR)
    public ResponseEntity<DoctorDto> oneDoctorById(@PathVariable Integer doctorId) {
        if (Objects.isNull(doctorId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok(doctorService.findDoctorById(doctorId));
    }
    @GetMapping(value = API_ALL_DOCTOR)
    public List<DoctorDto> allDoctors() {
        return doctorService.findAllDoctors();
    }

    @PostMapping(value = API_CREATE_DOCTOR, produces = MediaType.APPLICATION_JSON_VALUE)
    public DoctorDto createDoctor(@RequestBody DoctorDto doctorDto){
       DoctorEntity created =  doctorService.create(doctorMapper.mapFromDto(doctorDto));
       return doctorMapper.mapFromEntity(created);
    }
    @DeleteMapping(value = API_ONE_DOCTOR)
    public ResponseEntity<?> deleteDoctor(@PathVariable Integer doctorId){
        try{
            doctorService.deleteById(doctorId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
