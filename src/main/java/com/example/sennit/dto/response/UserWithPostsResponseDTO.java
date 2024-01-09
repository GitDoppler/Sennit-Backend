package com.example.sennit.dto.response;

import com.example.sennit.dto.common.PostWithVoteDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record UserWithPostsResponseDTO(
        String status,
        String message,
        Optional<String> profileName,
        Optional<Integer> reputationScore,
        Optional<LocalDateTime> createdAt,
        Optional<List<PostWithVoteDTO>> listPosts
) {
}
