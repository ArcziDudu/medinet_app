package com.medinet.api.controller;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.ChangePasswordForm;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.OpinionEntity;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientService patientService;
    private AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm z");

    private final String ACCOUNT_USER_ID = "/account/user/{userId}";
    private final String PASSWORD_CHANGE = "/password/change";


    @GetMapping(ACCOUNT_USER_ID)
    public String showUsersPage(@PathVariable("userId") Integer userId, Model model) {

        PatientDto currentPatient = patientService.findByUserId(userId);
        List<AppointmentDto> UpcomingAppointments = appointmentService.findUpcomingAppointments(currentPatient);
        List<AppointmentDto> completedAppointments = appointmentService.findCompletedAppointments(currentPatient);
        List<AppointmentDto> pendingAppointments = appointmentService.findPendingAppointments(currentPatient);
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();

        TreeSet<OpinionEntity> opinionsSortedByDate = currentPatient
                .getOpinions()
                .stream()
                .sorted(getOpinionEntityComparator())
                .collect(Collectors.toCollection(TreeSet::new));
        currentPatient.setOpinions(opinionsSortedByDate);
        model.addAttribute("passwordForm", changePasswordForm);
        model.addAttribute("CurrentPatient", currentPatient);
        model.addAttribute("format", formatter);
        model.addAttribute("UpcomingAppointments", UpcomingAppointments);
        model.addAttribute("CompletedAppointments", completedAppointments);
        model.addAttribute("pendingAppointments", pendingAppointments);
        return "myAccount";
    }

    private Comparator<? super OpinionEntity> getOpinionEntityComparator() {
        return Comparator.comparing(OpinionEntity::getDateOfCreateOpinion);
    }


    @PostMapping(PASSWORD_CHANGE)
    public String changePassword(@Valid @ModelAttribute("passwordForm") ChangePasswordForm passwordForm,
                                 BindingResult bindingResult,
                                 Model model,
                                 Principal principal) {
        String email = principal.getName();
        UserEntity currentUser = userRepository.findByEmail(email);
        int id = currentUser.getId();
        PatientDto currentPatient = patientService.findByUserId(id);
        List<AppointmentDto> UpcomingAppointments = appointmentService.findUpcomingAppointments(currentPatient);
        List<AppointmentDto> completedAppointments = appointmentService.findCompletedAppointments(currentPatient);
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();


        if (!passwordEncoder.matches(passwordForm.getCurrentPassword(), currentUser.getPassword())) {
            model.addAttribute("error", "aktualne hasło nieprawidłowe!");
            model.addAttribute("passwordForm", changePasswordForm);
            model.addAttribute("CurrentPatient", currentPatient);
            model.addAttribute("format", formatter);
            model.addAttribute("UpcomingAppointments", UpcomingAppointments);
            model.addAttribute("CompletedAppointments", completedAppointments);
            return "myAccount";
        }
        if (bindingResult.hasErrors()) {
            userRepository.save(currentUser);
            model.addAttribute("passwordForm", changePasswordForm);
            model.addAttribute("CurrentPatient", currentPatient);
            model.addAttribute("format", formatter);
            model.addAttribute("UpcomingAppointments", UpcomingAppointments);
            model.addAttribute("CompletedAppointments", completedAppointments);
            model.addAttribute("error",
                    "Nowe hasło musi składać się z minimum sześciu znaków oraz " +
                            "musi zawierać co najmniej jedną cyfrę.");
            return "myAccount";
        }

        currentUser.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        userRepository.save(currentUser);
        model.addAttribute("passwordForm", changePasswordForm);
        model.addAttribute("CurrentPatient", currentPatient);
        model.addAttribute("format", formatter);
        model.addAttribute("UpcomingAppointments", UpcomingAppointments);
        model.addAttribute("CompletedAppointments", completedAppointments);
        model.addAttribute("message", "Hasło zostało zmienione");

        return "myAccount";

    }

}
