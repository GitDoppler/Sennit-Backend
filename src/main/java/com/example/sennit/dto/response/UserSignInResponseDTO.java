package com.example.sennit.dto.response;

import java.util.List;
import java.util.Optional;

public record UserSignInResponseDTO(
        String status,
        String message,
        Optional<String> sessionID,
        Optional<String> username,
        Optional<List<String>> listRoles
) {
}
