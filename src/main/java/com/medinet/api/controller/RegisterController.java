package com.medinet.api.controller;

import com.medinet.api.dto.RegistrationFormDto;
import com.medinet.business.services.*;
import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.security.RoleEntity;
import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class RegisterController {
    private final UserRepository userRepository;
    private final PatientService patientService;
    private final RegisterService registerService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private JavaMailSender mailSender;
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

    @GetMapping("/password/reminder")
    public String showPasswordReminderForm(Model model) {
        model.addAttribute("email", "");
        return "PasswordRecovery";
    }

    @PostMapping("/password/recovery")
    public String recoveryPassword(@RequestParam("email") String email,Model model)  {
        RoleEntity doctorRole = roleRepository.findByRole("DOCTOR");
        if (!userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Ten email nie istnieje w bazie danych");
            return "PasswordRecovery";

        }
        else if (userRepository.findByEmail(email).getRoles().contains(doctorRole)) {
            model.addAttribute("error", "Ten email należy do lekarza!");
            return "PasswordRecovery";
        } else {
            String uuid = String.valueOf(UUID.randomUUID());
            UserEntity user = userRepository.findByEmail(email);
            user.setPassword(passwordEncoder.encode(uuid));
            userRepository.save(user);
            sendEmail(email, uuid);

        }

        return "redirect:/login";
    }



    void sendEmail(String recipientEmail, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setFrom("medinet.rezerwacje@gmail.com", "Support");
            helper.setTo(recipientEmail);

            String subject = "Oto twoje nowe hasło, możesz je zmienić lub nie";

            helper.setSubject(subject);

            String content = "<html><body>";
            content += "<h2>Twoje nowe hasło:</h2>";
            content += "<p>" + password + "</p>";
            content += "<p>Możesz teraz zalogować się za pomocą tego hasła i zmienić je na swoje preferowane.</p>";
            content += "</body></html>";

            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    @PostMapping("/register/save")
    public String registration(@Valid
                               @ModelAttribute("form") RegistrationFormDto form,
                               BindingResult result) {

        if (userRepository.existsByEmail(form.getEmail())) {
            result.rejectValue("email", "400", "Ten email jest już zarejestrowany");
        } else if (patientService.findByPhoneNumber(form.getPhoneNumber())) {
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
