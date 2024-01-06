package com.example.sennit.repository;

import com.example.sennit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByUserID(Long userID);

    User findUserByEmail(String email);

    User findUserByUsername(String username);
}
