package com.example.sennit.repository;

import com.example.sennit.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthRepository extends JpaRepository<Session,Long> {
    Session findSessionByUserID(Long userID);

    Session findSessionByStringID(String sessionID);
}
