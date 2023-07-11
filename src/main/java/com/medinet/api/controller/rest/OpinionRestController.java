package com.medinet.api.controller.rest;

import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.OpinionDto;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.OpinionService;
import com.medinet.business.services.PatientService;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(OpinionRestController.API_OPINION)
public class OpinionRestController {
    public static final String API_OPINION = "/api/opinion";
    public static final String API_OPINION_ALL = "/all";
    public static final String API_OPINION_BY_PATIENT = "/{patientId}";
    public static final String API_OPINION_BY_DOCTOR = "/{doctorId}";
    public static final String API_OPINION_CREATE = "/create/{patientId}/{doctorId}";

    private final OpinionService opinionService;
    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    @GetMapping(value = API_OPINION_ALL)
    public List<OpinionDto> allOpinions() {
        return opinionService.findAll();
    }
    @GetMapping(value = API_OPINION_BY_PATIENT)
    public List<OpinionDto> allOpinionsByPatient(@PathVariable Integer patientId) {
        return opinionService.findAll().stream()
                .filter(a->a.getPatient().getPatientId().equals(patientId))
                .toList();
    }
    @GetMapping(value = API_OPINION_BY_DOCTOR)
    public List<OpinionDto> allOpinionsByDoctor(@PathVariable Integer doctorId) {
        return opinionService.findAll().stream()
                .filter(a->a.getDoctor().getDoctorId().equals(doctorId))
                .toList();
    }
    @PostMapping(value = API_OPINION_CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createDoctor(
            @PathVariable Integer patientId,
            @PathVariable Integer doctorId,
            @RequestBody String opinion){


        try {
            doctorService.findDoctorById(doctorId);
            patientService.findById(patientId);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        OpinionEntity newOpinion = OpinionEntity.builder()
                .dateOfCreateOpinion(OffsetDateTime.now())
                .doctor(doctorMapper.mapFromDto(doctorService.findDoctorById(doctorId)))
                .patient(patientMapper.mapFromDto(patientService.findById(patientId)))
                .note(opinion)
                .build();

        opinionService.processOpinion(newOpinion);

        return ResponseEntity
                .ok(opinionService.processOpinion(newOpinion));
    }
}
