package com.example.sennit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupID;

    @NotBlank(message = "Subgroup name is mandatory")
    @Column(name="name")
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Column(name = "description")
    private String description;

    @Column(name = "created_by_user_id")
    private Long ownerID;

    @Column(name = "creation_date")
    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToMany(mappedBy = "listGroups", fetch = FetchType.LAZY)
    private List<User> members;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(groupID, group.groupID) && Objects.equals(name, group.name) && Objects.equals(description, group.description) && Objects.equals(ownerID, group.ownerID) && Objects.equals(createdAt, group.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupID, name, description, ownerID, createdAt);
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupID=" + groupID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ownerID=" + ownerID +
                ", createdAt=" + createdAt +
                '}';
    }
}
