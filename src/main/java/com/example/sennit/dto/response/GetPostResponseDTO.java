package com.example.sennit.dto.response;

import java.util.Optional;

public record GetPostResponseDTO(
        String status,
        String message,
        Optional<Long> postID,
        Optional<String> postTitle,
        Optional<String> postContent,
        Optional<String> writerName,
        Optional<Long> postScore,
        Optional<Integer> currentUserVote
) {
}
