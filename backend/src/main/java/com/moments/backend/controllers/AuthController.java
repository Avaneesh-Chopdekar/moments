package com.moments.backend.controllers;

import com.moments.backend.dto.AuthRequestDto;
import com.moments.backend.dto.JwtResponseDto;
import com.moments.backend.dto.LogoutResponseDto;
import com.moments.backend.entities.User;
import com.moments.backend.repositories.UserRepository;
import com.moments.backend.services.JwtService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @SecurityRequirements(value = {})
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody AuthRequestDto request) {
        try {
            Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("Username is already in use.");
            }

            String accessToken = jwtService.generateToken(request.getUsername(), true);
            String refreshToken = jwtService.generateToken(request.getUsername(), false);

            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setRefreshToken(refreshToken);

            userRepository.save(newUser);

            JwtResponseDto jwtResponse = new JwtResponseDto(accessToken, refreshToken);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
             e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @SecurityRequirements(value = {})
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String accessToken = jwtService.generateToken(request.getUsername(), true);
        String refreshToken = jwtService.generateToken(request.getUsername(), false);
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        user.get().setRefreshToken(refreshToken);
        userRepository.save(user.get());
        JwtResponseDto jwtResponse = new JwtResponseDto(accessToken, refreshToken);
        return ResponseEntity.ok().body(jwtResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String username = jwtService.getUsernameFromToken(token);
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (user.get().getRefreshToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String accessToken = jwtService.generateToken(username, true);
        String refreshToken = jwtService.generateToken(username, false);
        user.get().setRefreshToken(refreshToken);
        userRepository.save(user.get());
        JwtResponseDto jwtResponse = new JwtResponseDto(accessToken, refreshToken);
        return ResponseEntity.ok().body(jwtResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String username = jwtService.getUsernameFromToken(token);
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (user.get().getRefreshToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        user.get().setRefreshToken(null);
        userRepository.save(user.get());

        LogoutResponseDto logoutResponse = new LogoutResponseDto("Logged out successfully");
        return ResponseEntity.ok(logoutResponse);
    }
}