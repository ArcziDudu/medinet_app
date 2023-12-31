package com.medinet.api.controller.rest;

import com.medinet.api.dto.ExceptionMessage;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.domain.exception.PatientEmailConflictException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionRestHandler extends ResponseEntityExceptionHandler {

    private static final Map<Class<?>, HttpStatus> EXCEPTION_STATUS = Map.of(
            ConstraintViolationException.class, HttpStatus.BAD_REQUEST,
            DataIntegrityViolationException.class, HttpStatus.BAD_REQUEST,
            EntityNotFoundException.class, HttpStatus.NOT_FOUND,
            NotFoundException.class, HttpStatus.NOT_FOUND
    );

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception exception,
            @Nullable Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request
    ) {
        final String errorId = UUID.randomUUID().toString();
        log.error("Exception: ID={}, HttpStatus={}", errorId, statusCode, exception);
        return super.handleExceptionInternal(exception, ExceptionMessage.of(errorId), headers, statusCode, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(final Exception exception) {
        return doHandle(exception, getHttpStatusFromException(exception.getClass()));
    }

    private ResponseEntity<Object> doHandle(final Exception exception, final HttpStatus status) {
        final String errorId = UUID.randomUUID().toString();
        log.error("Exception: ID={}, HttpStatus={}", errorId, status, exception);

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionMessage.of(errorId));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleAppointmentNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    public HttpStatus getHttpStatusFromException(final Class<?> exception) {
        return EXCEPTION_STATUS.getOrDefault(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PatientEmailConflictException.class)
    public ResponseEntity<?> handlePatientEmailConflictException(PatientEmailConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<?> handleEmailNotFoundException(MailException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionMessage.of(ex.getMessage()));
    }

}
