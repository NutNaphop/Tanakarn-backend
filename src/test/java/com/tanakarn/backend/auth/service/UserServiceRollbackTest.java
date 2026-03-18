package com.tanakarn.backend.auth.service;

import com.tanakarn.backend.account.repository.AccountRepository;
import com.tanakarn.backend.auth.entity.User;
import com.tanakarn.backend.auth.repository.UserRepository;
import com.tanakarn.backend.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceRollbackTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldRollbackUserWhenAccountSavingFails() {
        // Arrange
        String username = "rollback-user";
        String rawPassword = "1234";

        when(passwordEncoder.encode(rawPassword)).thenReturn("encoded-password");

        doThrow(new RuntimeException("Account save failed"))
                .when(accountRepository).save(org.mockito.ArgumentMatchers.any());

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.registerUser(username, rawPassword)
        );

        // Assert
        assertNull(userRepository.findByUsername(username));
        assertNotNull(exception.getCause());
    }
}