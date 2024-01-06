package com.example.sennit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePostRequestDTO(
        @NotBlank(message = "Title is mandatory")
        String title,
        @NotBlank(message = "Content is mandatory")
        String content
) {
}
