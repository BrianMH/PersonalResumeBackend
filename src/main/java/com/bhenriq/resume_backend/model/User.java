package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

/**
 * Contains some general information about users. In this application, however, only authenticated users will be stored
 * in the database (as there is no current requirement to open up the application to non-authenticated users).
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class User implements UserDetails {
    @Id
    private String id;

    // And then record some elements that would be passed from the front-end's JWT creation
    @Column(name = "email",
            unique = true)
    private String email;
    @Column(name = "username")
    private String username;
    @Column(name = "image")
    private String image;

    // and also include the relevant role of the given user
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    // and their associated accounts
    @OneToMany(mappedBy = "owningUser",
                orphanRemoval = true)
    private Set<Account> accounts;

    // Normal ctor (w.o role pre-specification)
    public User(String email, String username) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.username = username;

        this.roles = new TreeSet<>();
        this.accounts = new HashSet<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public boolean addAuthority(Role authority) {
        return this.roles.add(authority);
    }

    public boolean removeAuthority(Role authority) {
        return this.roles.remove(authority);
    }

    public boolean addAccount(Account acc) {
        return this.accounts.add(acc);
    }

    public boolean removeAccount(Account acc) {
        return this.accounts.remove(acc);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return String.format("[User = (id = %s) - (email = %s) - (username = %s) - (roles = %s)]",
                id, email, username, roles);
    }
}
