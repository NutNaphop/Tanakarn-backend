package com.tanakarn.backend.security.jwt;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JWTServiceTest {
    private final String secretKey = "YnJ1c2hmb3JlaWduA29tZm9ydGFibGV0aWxsY2hvaWNltvVsaWdpb3VzZGlzaHBhdGg=";
    private final JwtService jwtService = new JwtService(secretKey);

    @Test
    void testGenerateToken(){
        long userId = 1L;
        String username = "testUser";
        String token = jwtService.generateToken(userId, username);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldValidateGeneratedToken(){
        long userId = 1L;
        String username = "testUser";
        String token = jwtService.generateToken(userId, username);

        boolean valid = jwtService.isValidToken(token);

        assertTrue(valid);
    }

    @Test
    void shouldExtractUserIdFromToken(){
        long userId = 1L;
        String username = "testUser";
        String token = jwtService.generateToken(userId, username);

        long extractedUserId = jwtService.extractIDFromToken(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    void shouldReturnFalseWhenTokenIsInvalid(){
        String invalidToken = "invalid.token";
        boolean valid = jwtService.isValidToken(invalidToken);

        assertFalse(valid);
    }
}
