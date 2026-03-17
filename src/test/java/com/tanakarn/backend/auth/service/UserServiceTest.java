package com.tanakarn.backend.auth.service;

import com.tanakarn.backend.account.entity.Account;
import com.tanakarn.backend.account.repository.AccountRepository;
import com.tanakarn.backend.auth.dto.response.LoginResponse;
import com.tanakarn.backend.auth.entity.User;
import com.tanakarn.backend.auth.repository.UserRepository;
import com.tanakarn.backend.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        String username = "naphop";
        String rawPassword = "1234";
        String encodedPassword = "encoded-password";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        // Act
        userService.registerUser(username, rawPassword);

        // Assert
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(userCaptor.capture());
        verify(accountRepository).save(accountCaptor.capture());

        User savedUser = userCaptor.getValue();
        Account savedAccount = accountCaptor.getValue();

        assert savedUser.getUsername().equals(username);
        assert savedUser.getPassword().equals(encodedPassword);
        assert savedAccount.getUser().equals(savedUser);
    }

    @Test
    void shouldThrowExceptionWhenRegisterFails() {
        // Arrange
        String username = "naphop";
        String rawPassword = "1234";

        when(passwordEncoder.encode(rawPassword)).thenThrow(RuntimeException.class);

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(username, rawPassword)
        );

        // Assert
        assertNotNull(exception.getCause());

        verify(userRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldLoginUserSuccessfully() {
        User user = new User();
        String token = "token";

        user.setId(1L);
        user.setUsername("naphop");
        user.setPassword("1234");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.generateToken(user.getId(), user.getUsername())).thenReturn(token);
        when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);

        LoginResponse response = userService.loginUser(user.getUsername(), user.getPassword());

        assertEquals(token, response.getToken());
        assertEquals(user.getId(), response.getAccountId());
        assertEquals(user.getUsername(), response.getUsername());

        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        User user = new User();
        user.setUsername("naphop");
        user.setPassword("1234");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.loginUser(user.getUsername(), user.getPassword())
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        final String rawPassword = "1234";

        User user = new User();
        user.setUsername("naphop");
        user.setPassword("1234");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.loginUser(user.getUsername(), rawPassword)
        );

        assertEquals("Invalid password", exception.getMessage());
    }
}
