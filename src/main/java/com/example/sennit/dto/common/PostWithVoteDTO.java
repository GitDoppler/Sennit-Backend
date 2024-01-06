package com.example.sennit.dto.common;

import java.time.LocalDateTime;

public record PostWithVoteDTO(
        Long postID,
        String postTitle,
        String postContent,
        LocalDateTime createdAt,
        String writerUsername,
        Long postScore,
        Integer currentUserVote
) {
}
