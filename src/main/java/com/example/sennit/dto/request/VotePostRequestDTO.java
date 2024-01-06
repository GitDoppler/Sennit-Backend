package com.example.sennit.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VotePostRequestDTO(
        @NotNull(message = "VoteType is mandatory")
        @Min(value = -1, message = "VoteType can only be -1, 0 or 1")
        @Max(value = 1, message = "VoteType can only be -1, 0 or 1")
        Integer voteType,
        @NotNull(message="PostID is mandatory")
        @Min(value = 0, message = "PostID must be greater than or equal to 0")
        Long postID
) {
}
