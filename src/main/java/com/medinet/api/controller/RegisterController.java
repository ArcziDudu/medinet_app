package com.medinet.api.controller;

import com.medinet.api.dto.RegistrationFormDto;
import com.medinet.business.services.PatientService;
import com.medinet.business.services.RegisterService;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Objects;
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
    private final String PASSWORD_REMINDER = "/password/reminder";
    private final String PASSWORD_RECOVERY = "/password/recovery";
    private final String REGISTER_SAVE = "/register/save";
    private final String VERIFICATION = "/verification";
    private final String ACCOUNT_IS_ACTIVE = "/account/is-active";

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

    @GetMapping(PASSWORD_REMINDER)
    public String showPasswordReminderForm(Model model) {
        model.addAttribute("email", "");
        return "PasswordRecovery";
    }

    @PostMapping(PASSWORD_RECOVERY)
    public String recoveryPassword(@RequestParam("email") String email, Model model) {
        RoleEntity doctorRole = roleRepository.findByRole("DOCTOR");
        if(Objects.isNull(email)){
            model.addAttribute("error", "Wprowadź adres email");
            return "PasswordRecovery";
        }
        if (!userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Ten email nie istnieje w bazie danych");
            return "PasswordRecovery";

        } else if (userRepository.findByEmail(email).getRoles().contains(doctorRole)) {
            model.addAttribute("error", "Ten email należy do lekarza!");
            return "PasswordRecovery";
        } else {
            String uuid = String.valueOf(UUID.randomUUID());
            UserEntity user = userRepository.findByEmail(email);
            user.setPassword(passwordEncoder.encode(uuid));
            userRepository.save(user);
            String emailSubject = "Oto twoje nowe hasło, możesz je zmienić lub nie";
            String text1 = "Twoje nowe hasło:";
            String text2 = "Możesz teraz zalogować się za pomocą tego hasła i zmienić je na swoje preferowane.";
            sendEmail(email, uuid, emailSubject, text1, text2);
        }

        return "redirect:/password/reminder?success=true";
    }


    void sendEmail(String recipientEmail, String password, String emailSubject, String text1, String text2) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setFrom("medinet.rezerwacje@gmail.com", "Support");
            helper.setTo(recipientEmail);


            helper.setSubject(emailSubject);

            String content = "<html><body>";
            content += "<h2>" + text1 + "</h2>";
            content += "<p>" + password + "</p>";
            content += "<p>" + text2 + "</p>";
            content += "</body></html>";

            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @PostMapping(REGISTER_SAVE)
    public String registration(@Valid
                               @ModelAttribute("form") RegistrationFormDto form,
                               BindingResult result, Model model) {

        if (userRepository.existsByEmail(form.getEmail())) {
            result.rejectValue("email", "400", "Ten email jest już zarejestrowany");
        } else if (patientService.findByPhoneNumber(form.getPhoneNumber())) {
            result.rejectValue("phoneNumber", "400", "Ten numer jest już zarejestrowany");
        }

        if (result.hasErrors()) {
            return "register";
        }
        UUID uuid = UUID.randomUUID();
        UserEntity newUser = UserEntity.builder()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .roles(new HashSet<>())
                .active(false)
                .verifyCode(String.valueOf(uuid))
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

        String emailSubject = "Aktywacja konta";
        String text1 = "Twój kod aktywacyjny:";
        String text2 = "Aktywuj swoje konto aby w pełni korzystać z serwisu Medinet.";
        sendEmail(form.getEmail(), String.valueOf(uuid), emailSubject, text1, text2);
        registerService.save(newUser);
        patientService.createNewPatient(newPatient);
        return "redirect:/register?success=true";

    }

    @GetMapping(VERIFICATION)
    public String verifyAccount() {
        return "accountActivate";
    }

    @PostMapping(ACCOUNT_IS_ACTIVE)
    public String verification(@RequestParam("email") String email,
                               @RequestParam("code") String code,
                               Model model) {
        UserEntity user = userRepository.findByEmail(email);
        RoleEntity doctorRole = roleRepository.findByRole("DOCTOR");
        if (!userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Ten email nie istnieje w bazie danych");
            return "accountActivate";
        } else if (user.getRoles().contains(doctorRole) || user.getEmail().equals("admin@admin.pl")) {
            model.addAttribute("error", "To konto nie wymaga aktywacji");
            return "accountActivate";
        } else if (userRepository.existsByEmail(email) && user.getActive()) {
            model.addAttribute("error", "To konto nie wymaga aktywacji");
            return "accountActivate";
        } else if (!user.getVerifyCode().equals(code)) {
            model.addAttribute("error", "Kod niepoprawny");
            return "accountActivate";
        }
        user.setActive(true);
        registerService.save(user);
        return "redirect:/verification?success=true";
    }
}
