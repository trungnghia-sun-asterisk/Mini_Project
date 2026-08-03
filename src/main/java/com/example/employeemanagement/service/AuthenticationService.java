package com.example.employeemanagement.service;

import java.util.Locale;

import com.example.employeemanagement.dto.request.LoginRequest;
import com.example.employeemanagement.dto.request.RegisterRequest;
import com.example.employeemanagement.dto.response.AuthResponse;
import com.example.employeemanagement.dto.response.RegistrationResponse;
import com.example.employeemanagement.entity.AppUser;
import com.example.employeemanagement.entity.Role;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.repository.AppUserRepository;
import com.example.employeemanagement.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(AppUserRepository appUserRepository,
                                  PasswordEncoder passwordEncoder,
                                  AuthenticationManager authenticationManager,
                                  JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegistrationResponse register(RegisterRequest request) {
        String username = normalizeUsername(request.username());
        if (appUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException("Username '" + username + "' is already registered");
        }
        AppUser user = appUserRepository.save(new AppUser(
                username, passwordEncoder.encode(request.password()), Role.USER));
        log.info("Registered user username={} role={}", user.getUsername(), user.getRole());
        return new RegistrationResponse(user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        String username = normalizeUsername(request.username());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .orElse(Role.USER.name());
        String token = jwtService.generateToken(userDetails);
        log.info("Successful login username={} role={}", userDetails.getUsername(), role);
        return new AuthResponse("Bearer", token, userDetails.getUsername(), role);
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
}
