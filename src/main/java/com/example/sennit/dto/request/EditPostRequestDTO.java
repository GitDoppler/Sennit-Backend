package com.example.sennit.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditPostRequestDTO(
        @NotNull(message="PostID is mandatory")
        @Min(value = 0, message = "PostID must be greater than or equal to 0")
        Long postID,
        @NotBlank(message = "Title is mandatory")
        String title,
        @NotBlank(message = "Content is mandatory")
        String content
) {
}
