package com.tanakarn.backend.auth.controller;

import com.tanakarn.backend.auth.dto.request.AuthRequest;
import com.tanakarn.backend.auth.dto.response.LoginResponse;
import com.tanakarn.backend.auth.service.UserService;
import com.tanakarn.backend.common.response.ApiResponse;
import com.tanakarn.backend.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AuthController {
    final UserService userService;
    final JwtService jwtService;

    AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/api/auth/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody AuthRequest request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            userService.registerUser(username, password);
            return ResponseEntity.ok().body(new ApiResponse<>(true, "User registered successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }

    }

    @Operation(summary = "Login a user")
    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody AuthRequest request){
        try{
            String username = request.getUsername();
            String password = request.getPassword();
            LoginResponse res =  userService.loginUser(username, password);

            return ResponseEntity.ok().body(new ApiResponse<>(true, "Login successful", res));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Validate user token")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/auth/validate")
    public ResponseEntity<ApiResponse<Void>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (jwtService.isValidToken(token.substring(7))) {
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.status(401).build();
    }
}
