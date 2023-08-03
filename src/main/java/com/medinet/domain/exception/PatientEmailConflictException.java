package com.medinet.domain.exception;

public class PatientEmailConflictException extends RuntimeException{
    public PatientEmailConflictException(final String message) {
        super(message);
    }
}
