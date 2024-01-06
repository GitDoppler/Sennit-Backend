package com.example.sennit.dto.response;

import com.example.sennit.dto.common.PostWithVoteDTO;

import java.util.List;
import java.util.Optional;

public record TopPostsResponseDTO(
        String status,
        String message,
        Optional<List<PostWithVoteDTO>> listPosts
) {
}
