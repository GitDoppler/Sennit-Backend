package com.example.sennit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditGroupRequestDTO(
        @NotNull(message="Group id is mandatory")
        Long groupID,
        @NotBlank(message = "Group name is mandatory")
        String name,
        @NotBlank(message = "Description is mandatory")
        String description
) {
}
