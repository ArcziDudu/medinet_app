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
public class RegistrationFormDto {
    @NotEmpty(message = "Wprowadź swoje imię")
    private String name;
    @NotEmpty(message = "Wprowadź swoje nazwisko")
    private String surname;

    @NotEmpty(message = "Wprowadź adres email!")
    @Email(message = "Wprowadź poprawny adres email!")
    private String email;

    @Size(min = 6, message = "hasło musi składać się z minimum sześciu znaków")
    @Pattern(regexp = "^(?=.*[0-9]).{6,}$", message = "Hasło musi zawierać co najmniej jedną cyfrę.")
    @NotEmpty(message = "Hasło nie może być puste")
    private String password;
    @NotEmpty(message = "Twój adres jest potrzebny do rejestracji - wprowadź miasto")
    private String city;
    @Pattern(regexp = "^\\d{2}-\\d{3}$", message = "Kod pocztowy musi mieć format xx-xxx")
    @NotEmpty(message = "Twój adres jest potrzebny do rejestracji - wprowadź kod pocztowy")
    private String postalCode;
    @NotEmpty(message = "Twój adres jest potrzebny do rejestracji - wprowadź adres")
    private String street;

    @NotEmpty(message = "Wprowadź numer telefonu")
    @Pattern(regexp = "^[+]\\d{2}\\s\\d{3}\\s\\d{3}\\s\\d{3}$", message = "Numer telefonu musi mieć format +xx xxx xxx xxx")
    private String phoneNumber;
}
