package com.example.sennit.dto.response;

import java.util.Optional;

public record CreatePostResponseDTO(
        String status,
        String message,
        Optional<Long> postID
) {
}
