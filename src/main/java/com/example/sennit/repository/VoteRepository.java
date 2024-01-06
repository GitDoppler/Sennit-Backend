package com.example.sennit.repository;

import com.example.sennit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote,Long> {
    Vote findVoteByUserIDAndPostID(Long userID, Long postID);
}
