package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class EmailWhitelist {
    @Id
    String email;

    // for every whitelist email, we have a default set of roles that would be applied to the email
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;
}
