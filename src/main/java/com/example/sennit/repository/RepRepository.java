package com.example.sennit.repository;

import com.example.sennit.model.Reputation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepRepository extends JpaRepository<Reputation,Long> {
    Reputation findReputationByUserID(Long userID);

    @Query("SELECT r FROM Reputation r, Post p WHERE p.userID = r.userID AND p.postID = :postID")
    Reputation findReputationByPostId(Long postID);
}
