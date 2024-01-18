package com.example.sennit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignUpRequestDTO(
        @NotBlank(message = "Profile name is mandatory")
        String username,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 1, max = 40, message = "Password must be between 5 and 20 characters")
        String password,
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        String email
) {
}
