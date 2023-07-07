package com.medinet.api.controller.rest;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
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
    public final String API_ALL_DOCTOR = "/all";
    public final String API_ALL_DOCTOR_PAGE = "/all/{page}";
    public final String API_ONE_DOCTOR = "/{doctorId}";
    public final String API_CREATE_DOCTOR = "/create";
    public static final String DOCTOR_ID_RESULT = "/%s";
    public final String DOCTOR_UPDATE_BIO = "/{doctorId}/{newBio}";
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
        try{
            doctorService.deleteById(doctorId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(API_ONE_DOCTOR)
    public ResponseEntity<?> updateDoctor(
            @PathVariable Integer doctorId,
            @RequestBody DoctorDto doctorDto
    ) {
        DoctorEntity existingDoctor = doctorMapper.mapFromDto(doctorService.findDoctorById(doctorId));

        existingDoctor.setName(doctorDto.getName());
        existingDoctor.setSurname(doctorDto.getSurname());
        existingDoctor.setEmail(doctorDto.getEmail());
        existingDoctor.setPriceForVisit(doctorDto.getPriceForVisit());
        existingDoctor.setAddress(doctorDto.getAddress());
        existingDoctor.setCalendars(doctorDto.getCalendars());
        existingDoctor.setAppointments(doctorDto.getAppointments());
        existingDoctor.setOpinions(doctorDto.getOpinions());
        doctorService.create(existingDoctor);
        return ResponseEntity.ok().build();
    }

//    @PatchMapping(DOCTOR_UPDATE_BIO)
//    public ResponseEntity<?> updateEmployeeSalary(
//            @PathVariable Integer doctorId,
//            @RequestParam(required = true) String newBio
//    ) {
//        DoctorEntity existingDoctor = doctorMapper.mapFromDto(doctorService.findDoctorById(doctorId));
//        existingDoctor.setBio(newBio);
//        doctorService.create(existingDoctor);
//        return ResponseEntity.ok().build();
//    }
}
