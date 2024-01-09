package com.example.sennit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "Reputation")
public class Reputation {
    @Id
    @Column(name = "user_id")
    private Long userID;

    @Column(name = "reputation_score")
    private Integer reputationScore;

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Integer getReputationScore() {
        return reputationScore;
    }

    public void setReputationScore(Integer reputationScore) {
        this.reputationScore = reputationScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reputation that = (Reputation) o;
        return Objects.equals(userID, that.userID) && Objects.equals(reputationScore, that.reputationScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, reputationScore);
    }

    @Override
    public String toString() {
        return "Reputation{" +
                "userID=" + userID +
                ", reputationScore=" + reputationScore +
                '}';
    }
}
