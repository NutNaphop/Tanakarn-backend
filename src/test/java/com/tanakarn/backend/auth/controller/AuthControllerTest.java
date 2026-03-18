package com.tanakarn.backend.auth.controller;

import com.tanakarn.backend.auth.dto.request.AuthRequest;
import com.tanakarn.backend.auth.service.UserService;
import com.tanakarn.backend.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        AuthRequest req = new AuthRequest();
        req.setUsername("naphop");
        req.setPassword("1234");

        String requestBody = objectMapper.writeValueAsString(req);
        doNothing().when(userService).registerUser(req.getUsername(), req.getPassword());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        verify(userService).registerUser(req.getUsername(), req.getPassword());
    }

    @Test
    void shouldThrow400WhenRegisterUserFails() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setUsername("naphop");
        req.setPassword("1234");

        doThrow(new RuntimeException("Register Fail")).when(userService).registerUser(req.getUsername(), req.getPassword());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(userService).registerUser(req.getUsername(), req.getPassword());
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setUsername("naphop");
        req.setPassword("1234");

        when(userService.loginUser(req.getUsername(), req.getPassword())).thenReturn(null);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));

        verify(userService).loginUser(req.getUsername(), req.getPassword());
    }

    @Test
    void shouldThrow400WhenLoginFail() throws Exception{
        AuthRequest req = new AuthRequest();
        req.setUsername("naphop");
        req.setPassword("1234");

        doThrow(new RuntimeException("Login Fail")).when(userService).loginUser(req.getUsername(), req.getPassword());
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Login Fail"));

        verify(userService).loginUser(req.getUsername(), req.getPassword());
    }

}
