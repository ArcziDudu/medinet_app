package com.medinet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {
    private String name;
    private String surname;
    private String email;
    private String password;
    private String city;
    private String street;
    private String postalCode;
    private String phoneNumber;
}
