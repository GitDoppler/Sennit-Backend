package com.example.sennit.repository;

import com.example.sennit.model.Post;
import com.example.sennit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findPostsByUserID(Long userID);

    @Query(value = "SELECT p.*, u.username, " +
            "COALESCE(total_votes.total, 0) as total_votes, " +
            "COALESCE(user_vote.vote_type, 0) as user_vote " +
            "FROM posts p " +
            "INNER JOIN users u ON p.user_id = u.user_id " +
            "LEFT JOIN ( " +
            "    SELECT post_id, SUM(vote_type) as total " +
            "    FROM votes " +
            "    GROUP BY post_id " +
            ") total_votes ON p.post_id = total_votes.post_id " +
            "LEFT JOIN ( " +
            "    SELECT post_id, vote_type " +
            "    FROM votes " +
            "    WHERE user_id = :userID " +
            ") user_vote ON p.post_id = user_vote.post_id " +
            "ORDER BY total_votes DESC " +
            "LIMIT 20",
            nativeQuery = true)
    List<Object[]> findTopPostsByUserID(@Param("userID") Long userID);

    @Query(value = "SELECT " +
            "p.post_id, " +
            "p.title, " +
            "p.content, " +
            "p.created_at, " +
            "COALESCE(SUM(v.vote_type), 0) AS score, " +
            "COALESCE((SELECT v.vote_type FROM votes v WHERE v.post_id = p.post_id AND v.user_id = :currentUserID), 0) AS currentUserVote " +
            "FROM posts p " +
            "LEFT JOIN votes v ON p.post_id = v.post_id " +
            "WHERE p.user_id = :searchedUserID " +
            "GROUP BY p.post_id " +
            "ORDER BY score DESC " +
            "LIMIT 10",
            nativeQuery = true)
    List<Object[]> findPostsByUserID(@Param("searchedUserID") Long searchedUserID, @Param("currentUserID") Long currentUserID);

    Post findPostByPostID(Long postID);

    @Query(value = "SELECT " +
            "p.post_id, " +
            "p.title, " +
            "p.content, " +
            "u.username, " +
            "COALESCE(SUM(v.vote_type), 0) AS total_score, " +
            "COALESCE((SELECT vote_type FROM votes WHERE user_id = :currentUserID AND post_id = p.post_id), 0) AS current_user_vote " +
            "FROM posts p " +
            "LEFT JOIN users u ON p.user_id = u.user_id " +
            "LEFT JOIN votes v ON p.post_id = v.post_id " +
            "WHERE p.post_id = :postID " +
            "GROUP BY p.post_id, u.username",
            nativeQuery = true)
    List<Object[]> findPostWithVoteByPostID(@Param("postID") Long postID, @Param("currentUserID") Long currentUserID);

}
