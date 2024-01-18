package com.example.sennit.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record GetGroupResponseDTO(
        String status,
        String message,
        Optional<String> name,
        Optional<String> description,
        Optional<LocalDateTime> createdAt,
        Optional<List<String>> listMembers,
        Optional<Boolean> isMember
) {
}
