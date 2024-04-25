package com.bhenriq.resume_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Role implements GrantedAuthority, Comparable<Role> {
    @Id
    @Column(name = "id")
    private String authority;

    @Override
    public String toString() {
        return "[Role - (" + authority + ")]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(authority, role.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }

    @Override
    public int compareTo(Role o) {
        return this.getAuthority().compareTo(o.getAuthority());
    }
}
