package com.medinet.api.controller.rest;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.security.RoleEntity;
import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping(DoctorRestController.API_DOCTOR)
public class DoctorRestController {
    private DoctorMapper doctorMapper;
    public static final String API_DOCTOR = "/api/doctor";
    public final String API_ALL_DOCTOR = "/all";
    public final String API_ALL_DOCTOR_PAGE = "/all/{page}";
    public final String API_ONE_DOCTOR = "/{doctorId}";
    public final String API_CREATE_DOCTOR = "/create";
    public static final String DOCTOR_ID_RESULT = "/%s";
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
    @GetMapping(API_ALL_DOCTOR_PAGE)
    public ResponseEntity<Page<DoctorDto>> getAllDoctors(
            @RequestParam(defaultValue = "0") Integer page
    ) {
        Page<DoctorDto> doctorsPage = doctorService.findAllDoctors(page);
        return ResponseEntity.ok(doctorsPage);
    }


    @PostMapping(value = API_CREATE_DOCTOR, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DoctorDto> createDoctor(@RequestBody DoctorDto doctorDto){

       DoctorEntity created =  doctorService.create(doctorMapper.mapFromDto(doctorDto));
       return ResponseEntity
               .created(URI.create(API_CREATE_DOCTOR + DOCTOR_ID_RESULT.formatted(created.getDoctorId())))
               .build();
    }
    @DeleteMapping(value = API_ONE_DOCTOR)
    public ResponseEntity<?> deleteDoctor(@PathVariable Integer doctorId){
            doctorService.deleteById(doctorId);
            return ResponseEntity.ok().build();
    }

}
