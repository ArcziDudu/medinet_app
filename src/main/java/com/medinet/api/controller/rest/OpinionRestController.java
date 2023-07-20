package com.medinet.api.controller.rest;

import com.medinet.api.dto.OpinionDto;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.OpinionService;
import com.medinet.business.services.PatientService;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(OpinionRestController.API_OPINION)
public class OpinionRestController {
    public static final String API_OPINION = "/api/opinion";
    public static final String API_OPINION_ALL = "/all";
    public static final String API_OPINION_BY_PATIENT = "/patient/{patientId}";
    public static final String API_OPINION_BY_DOCTOR = "/doctor/{doctorId}";
    public static final String API_OPINION_CREATE = "/create/{patientId}/{doctorId}";

    private final OpinionService opinionService;
    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    @GetMapping(value = API_OPINION_ALL)
    @Operation(summary = "Get all opinions", description = "Retrieve all opinions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opinions found")
    })
    public List<OpinionDto> allOpinions() {
        return opinionService.findAll();
    }

    @GetMapping(value = API_OPINION_BY_PATIENT)
    @Operation(summary = "Get opinions by patient ID", description = "Retrieve opinions based on the patient ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opinions found")
    })
    public List<OpinionDto> allOpinionsByPatient(@PathVariable Integer patientId) {
        return opinionService.findAll().stream()
                .filter(opinion -> opinion.getPatient().getPatientId().equals(patientId))
                .toList();
    }


    @GetMapping(value = API_OPINION_BY_DOCTOR)
    @Operation(summary = "Get opinions by doctor ID", description = "Retrieve opinions based on the doctor ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opinions found")
    })
    public List<OpinionDto> allOpinionsByDoctor(@PathVariable Integer doctorId) {
        return opinionService.findAll().stream()
                .filter(opinion -> opinion.getDoctor().getDoctorId().equals(doctorId))
                .toList();
    }

    @PostMapping(value = API_OPINION_CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create an opinion", description = "Create a new opinion based on the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opinion created"),
            @ApiResponse(responseCode = "400", description = "Invalid patient ID or doctor ID")
    })
    public ResponseEntity<?> createOpinion(
            @PathVariable @Parameter(description = "Patient ID") Integer patientId,
            @PathVariable @Parameter(description = "Doctor ID") Integer doctorId,
            @RequestBody @Schema(description = "Opinion text") String opinion) {

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

        return ResponseEntity.ok(opinionService.processOpinion(newOpinion));
    }

}
