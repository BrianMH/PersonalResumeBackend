package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "provider", "providerAccountId"
        })
})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "type",
            nullable = false)
    String type;
    @Column(name = "provider",
            nullable = false)
    String provider;
    @Column(name = "provider_account_id")
    String providerAccountId;

    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "expires")
    private Long tokenExpiry;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owning_user_id")
    private User owningUser;

    public Instant getInstantTokenExpiry() {
        return Instant.ofEpochSecond(this.tokenExpiry);
    }

    public boolean isCredentialsNonExpired() {
        return tokenExpiry == null || Instant.now().compareTo(getInstantTokenExpiry()) <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(type, account.type) && Objects.equals(provider, account.provider) && Objects.equals(providerAccountId, account.providerAccountId) && Objects.equals(accessToken, account.accessToken) && Objects.equals(tokenExpiry, account.tokenExpiry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, provider, providerAccountId, accessToken, tokenExpiry);
    }
}
