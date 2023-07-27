package com.medinet.business.services;

import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RegisterService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    public void save(UserEntity newUser) {

        newUser.getRoles().add(roleRepository.findByRole("PATIENT"));
        userRepository.save(newUser);
    }

    public Boolean exists(String email) {
        return userRepository.existsByEmail(email);
    }
}
