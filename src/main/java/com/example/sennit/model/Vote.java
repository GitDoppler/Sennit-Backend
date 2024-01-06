package com.example.sennit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="vote_id")
    private Long voteID;

    @Column(name = "user_id")
    @NotNull(message="UserID is mandatory")
    private Long userID;

    @Column(name = "post_id")
    @NotNull(message = "PostID is mandatory")
    private Long postID;

    @Column(name = "vote_type")
    @NotNull(message = "VoteType is mandatory")
    private Integer voteType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt=LocalDateTime.now();
    }

    public Long getVoteID() {
        return voteID;
    }

    public void setVoteID(Long voteID) {
        this.voteID = voteID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public Integer getVoteType() {
        return voteType;
    }

    public void setVoteType(Integer voteType) {
        this.voteType = voteType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(voteID, vote.voteID) && Objects.equals(userID, vote.userID) && Objects.equals(postID, vote.postID) && Objects.equals(voteType, vote.voteType) && Objects.equals(createdAt, vote.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voteID, userID, postID, voteType, createdAt);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "voteID=" + voteID +
                ", userID=" + userID +
                ", postID=" + postID +
                ", voteType=" + voteType +
                ", createdAt=" + createdAt +
                '}';
    }
}
