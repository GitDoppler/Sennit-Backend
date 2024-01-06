package com.example.sennit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignInRequestDTO(
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 5, max = 20, message = "Password must be between 5 and 20 characters")
        String password
) {
}
