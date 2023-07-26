package com.medinet.api.dto;

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
public class ChangePasswordForm {

    @NotEmpty(message = "Aktualne hasło nie może być puste")
    private String currentPassword;

    @NotEmpty(message = "Nowe hasło nie może być puste")
    @Size(min = 6, message = "Nowe hasło musi składać się z minimum sześciu znaków")
    @Pattern(regexp = "^(?=.*[0-9]).{6,}$", message = "Nowe hasło musi zawierać co najmniej jedną cyfrę.")
    private String newPassword;

}