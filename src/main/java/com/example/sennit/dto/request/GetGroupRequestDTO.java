package com.example.sennit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GetGroupRequestDTO(
        @NotBlank(message = "Group name is mandatory")
        String name
) {
}
