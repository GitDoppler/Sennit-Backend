package com.example.sennit.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleID;

    @Column(name = "user_id")
    private Long userID;

    @Column(name="role_type_id")
    private Long roleTypeID;

    public Long getRoleID() {
        return roleID;
    }

    public void setRoleID(Long roleID) {
        this.roleID = roleID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getRoleTypeID() {
        return roleTypeID;
    }

    public void setRoleTypeID(Long roleTypeID) {
        this.roleTypeID = roleTypeID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(roleID, role.roleID) && Objects.equals(userID, role.userID) && Objects.equals(roleTypeID, role.roleTypeID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleID, userID, roleTypeID);
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleID=" + roleID +
                ", userID=" + userID +
                ", roleTypeID=" + roleTypeID +
                '}';
    }
}
