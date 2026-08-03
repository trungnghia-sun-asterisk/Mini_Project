package com.example.employeemanagement.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;

import javax.crypto.SecretKey;

import com.example.employeemanagement.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties properties, Environment environment) {
        this.properties = properties;
        this.signingKey = createSigningKey(properties.getSecret(), environment);
    }

    public String generateToken(UserDetails userDetails) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + properties.getExpirationMs());
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", role)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject().equals(userDetails.getUsername())
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey createSigningKey(String configuredSecret, Environment environment) {
        if (configuredSecret == null || configuredSecret.isBlank()) {
            if (environment.acceptsProfiles(Profiles.of("prod"))) {
                throw new IllegalStateException("JWT_SECRET must be configured when the prod profile is active");
            }
            byte[] generatedSecret = new byte[32];
            new SecureRandom().nextBytes(generatedSecret);
            log.warn("JWT_SECRET is not configured; using an ephemeral signing key for this process");
            return Keys.hmacShaKeyFor(generatedSecret);
        }

        byte[] decodedSecret;
        try {
            decodedSecret = Decoders.BASE64.decode(configuredSecret);
        } catch (IllegalArgumentException exception) {
            decodedSecret = configuredSecret.getBytes(StandardCharsets.UTF_8);
        }
        if (decodedSecret.length < 32) {
            byte[] rawSecret = configuredSecret.getBytes(StandardCharsets.UTF_8);
            if (rawSecret.length >= 32) {
                decodedSecret = rawSecret;
            } else {
                throw new IllegalStateException("JWT_SECRET must contain at least 256 bits");
            }
        }
        return Keys.hmacShaKeyFor(decodedSecret);
    }
}
