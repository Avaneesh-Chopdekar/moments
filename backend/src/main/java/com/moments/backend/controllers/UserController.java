package com.moments.backend.controllers;

import com.moments.backend.dto.UserResponseDto;
import com.moments.backend.entities.User;
import com.moments.backend.services.JwtService;
import com.moments.backend.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user")
@Tag(name = "User API")
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String username = jwtService.getUsernameFromToken(token);
        Optional<User> user = userService.getUser(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (user.get().getRefreshToken() == null || user.get().getRefreshToken().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String accessToken = jwtService.generateToken(username, true);
        String refreshToken = jwtService.generateToken(username, false);
        UserResponseDto response = new UserResponseDto(username, user.get().getEmail(), user.get().getImage(), user.get().getRole(), user.get().isEnabled());
        return ResponseEntity.ok(response);
    }
}