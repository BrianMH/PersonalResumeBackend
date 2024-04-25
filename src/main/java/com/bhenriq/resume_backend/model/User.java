package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // And then record some elements that would be passed from the front-end's JWT creation
    @Column(name = "email")
    private String email;
    @Column(name = "username")
    private String username;
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "expires")
    private Instant tokenExpiry;

    // and also include the relevant role of the given user
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    // Normal ctor (w.o role pre-specification)
    public User(String email, String username, String accessToken, Instant tokenExpiry) {
        this.email = email;
        this.username = username;
        this.accessToken = accessToken;
        this.tokenExpiry = tokenExpiry;

        this.roles = new TreeSet<>();
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
        return tokenExpiry == null || Instant.now().compareTo(tokenExpiry) <= 0;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
