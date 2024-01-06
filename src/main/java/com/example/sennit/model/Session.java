package com.example.sennit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="session_id")
    private Long id;

    @Column(name="session_string")
    @NotBlank(message="StringID is mandatory")
    private String stringID;

    @Column(name="expiration_date")
    private LocalDateTime expirationDate;

    @Column(name="user_id")
    @NotNull(message = "UserID is mandatory")
    private Long userID;

    @PrePersist
    protected void onCreate(){
        expirationDate=LocalDateTime.now().plusMinutes(30);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStringID() {
        return stringID;
    }

    public void setStringID(String stringID) {
        this.stringID = stringID;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id) && Objects.equals(stringID, session.stringID) && Objects.equals(expirationDate, session.expirationDate) && Objects.equals(userID, session.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stringID, expirationDate, userID);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", stringID='" + stringID + '\'' +
                ", expirationDate=" + expirationDate +
                ", userID=" + userID +
                '}';
    }
}
