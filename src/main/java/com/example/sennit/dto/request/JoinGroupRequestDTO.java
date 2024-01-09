package com.example.sennit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JoinGroupRequestDTO(
        @NotBlank(message = "Group name is mandatory")
        String name
) {
}
