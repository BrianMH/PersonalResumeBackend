package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("email")
    private String email;
    @JsonProperty("username")
    private String username;
    @JsonProperty("image")
    private String image;

    @JsonProperty("roles")
    private List<RoleDTO> roles;

    // We never want to return multiple accounts simultaneously, but we do allow for a single account
    // to be associated with the User object in case the front-end asks for it
    @JsonProperty("account")
    private AccountDTO relevantAccount;

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                ", username='" + username + '\'' +
                ", roles=" + roles + '\'' +
                ", account=" + relevantAccount +
                '}';
    }
}
