package com.moments.backend.dto;


import com.moments.backend.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    private String image;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotBlank
    private boolean isEnabled;
}
