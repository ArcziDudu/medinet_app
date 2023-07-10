package com.medinet.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotEmpty(message = "Wprowadź adres email!")
    @Email(message = "Wprowadź poprawny adres email!")
    private String email;

    @Size(min = 6, message = "hasło musi składać się z minimum sześciu znaków")
    @Pattern(regexp = "^(?=.*[0-9]).{6,}$", message = "Hasło musi zawierać co najmniej jedną cyfrę.")
    @NotEmpty(message = "Hasło nie może być puste")
    private String password;

    private String city;
    private String street;
    private String postalCode;


    @Pattern(regexp = "^[+]\\d{2}\\s\\d{3}\\s\\d{3}\\s\\d{3}$")
    private String phoneNumber;
}
