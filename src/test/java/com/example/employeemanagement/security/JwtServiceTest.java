package com.example.employeemanagement.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.SecureRandom;
import java.util.Base64;

import com.example.employeemanagement.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockEnvironment;

class JwtServiceTest {

    @Test
    void createsAndValidatesTokenWithConfiguredKey() {
        JwtProperties properties = new JwtProperties();
        byte[] secret = new byte[32];
        new SecureRandom().nextBytes(secret);
        properties.setSecret(Base64.getEncoder().encodeToString(secret));
        properties.setExpirationMs(60_000L);
        JwtService jwtService = new JwtService(properties, new StandardEnvironment());
        UserDetails user = User.withUsername("alice").password("ignored").roles("USER").build();

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void refusesMissingSecretInProductionProfile() {
        JwtProperties properties = new JwtProperties();
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("prod");

        assertThatThrownBy(() -> new JwtService(properties, environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
    }
}
