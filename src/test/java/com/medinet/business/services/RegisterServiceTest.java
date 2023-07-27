package com.medinet.business.services;

import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RegisterService registerService;

    @Test
    public void testExists() {
        // given
        String email = "test@test.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // when
        Boolean exists = registerService.exists(email);

        // then
        Assertions.assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail(email);
    }
}
