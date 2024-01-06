package com.example.sennit.repository;

import com.example.sennit.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role,Long> {
    @Query(value = "SELECT r.user_id AS userId, rt.role_name AS roleName " +
            "FROM roles r " +
            "INNER JOIN role_types rt ON r.role_type_id = rt.role_type_id " +
            "WHERE r.user_id = :userID",
            nativeQuery = true)
    List<Object[]> findRoleByUserID(@Param("userID") Long userID);
}
