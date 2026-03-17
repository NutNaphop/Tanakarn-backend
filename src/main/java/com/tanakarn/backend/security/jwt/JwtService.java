package com.tanakarn.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final long TOKEN_LIFETIME_MS = 1000L * 60 * 60 * 10;

    private final String secretKey;

    public JwtService(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(long userId, String username) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userId);

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(now))
                .expiration(new Date(now + TOKEN_LIFETIME_MS))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public long extractIDFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("userId", Long.class);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}