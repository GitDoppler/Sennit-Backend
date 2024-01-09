package com.example.sennit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupRequestDTO(
        @NotBlank(message = "Group name is mandatory")
        String name,
        @NotBlank(message = "Description is mandatory")
        String description
) {
}
