package com.example.employeemanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.employeemanagement.dto.request.LoginRequest;
import com.example.employeemanagement.dto.request.RegisterRequest;
import com.example.employeemanagement.dto.response.AuthResponse;
import com.example.employeemanagement.dto.response.RegistrationResponse;
import com.example.employeemanagement.entity.AppUser;
import com.example.employeemanagement.entity.Role;
import com.example.employeemanagement.repository.AppUserRepository;
import com.example.employeemanagement.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void registerHashesPasswordAndAssignsUserRole() {
        RegisterRequest request = new RegisterRequest(" Alice ", "strong-password");
        when(appUserRepository.existsByUsernameIgnoreCase("alice")).thenReturn(false);
        when(passwordEncoder.encode("strong-password")).thenReturn("hashed-password");
        when(appUserRepository.save(any(AppUser.class)))
                .thenReturn(new AppUser("alice", "hashed-password", Role.USER));

        RegistrationResponse response = authenticationService.register(request);

        assertThat(response).isEqualTo(new RegistrationResponse("alice", "USER"));
        verify(passwordEncoder).encode("strong-password");
    }

    @Test
    void loginReturnsBearerTokenAndRole() {
        UserDetails user = User.withUsername("alice").password("hashed").roles("USER").build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authenticationService.login(new LoginRequest(" Alice ", "password"));

        assertThat(response).isEqualTo(new AuthResponse("Bearer", "jwt-token", "alice", "USER"));
    }
}
