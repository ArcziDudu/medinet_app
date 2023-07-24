package com.medinet.api.controller;

import com.medinet.api.dto.RegistrationFormDto;
import com.medinet.business.services.*;
import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;

@Controller
@AllArgsConstructor
public class RegisterController {
    private final UserRepository userRepository;
    private final PatientService patientService;
    private final RegisterService registerService;
    private final PasswordEncoder passwordEncoder;
    static final String LOGIN = "/login";
    static final String REGISTER = "/register";

    @GetMapping(LOGIN)
    public String login() {
        return "login";
    }

    @GetMapping(REGISTER)
    public String showRegistrationForm(Model model) {
        RegistrationFormDto form = new RegistrationFormDto();
        model.addAttribute("form", form);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid
                               @ModelAttribute("form") RegistrationFormDto form,
                               BindingResult result) {

        if (userRepository.existsByEmail(form.getEmail())) {
            result.rejectValue("email", "400", "Ten email jest już zarejestrowany");
        }
        else if (patientService.findByPhoneNumber(form.getPhoneNumber())) {
            result.rejectValue("phoneNumber", "400", "Ten numer jest już zarejestrowany");
        }

        if (result.hasErrors()) {
            return "register";
        }
        UserEntity newUser = UserEntity.builder()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .roles(new HashSet<>())
                .active(true)
                .build();
        PatientEntity newPatient = PatientEntity.builder()
                .name(form.getName())
                .surname(form.getSurname())
                .email(form.getEmail())
                .phoneNumber(form.getPhoneNumber())
                .address(AddressEntity.builder()
                        .country("Polska")
                        .city(form.getCity())
                        .street(form.getStreet())
                        .postalCode(form.getPostalCode())
                        .build())
                .user(newUser)
                .build();
        registerService.save(newUser);
        patientService.createNewPatient(newPatient);
        return "redirect:/booking";

    }


}
