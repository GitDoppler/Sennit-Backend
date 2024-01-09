package com.example.sennit.repository;

import com.example.sennit.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group,Long> {
    Group findGroupByGroupID(Long groupID);

    Group findGroupByName(String name);
}
