package com.example.sennit.dto.response;

import com.example.sennit.dto.common.PostWithVoteDTO;

import java.util.List;
import java.util.Optional;

public record UserSignUpResponseDTO(
        String status,
        String message,
        Optional<String> sessionID,
        Optional<String> username,
        Optional<List<String>> listRoles
) {
}
