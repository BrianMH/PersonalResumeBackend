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
    @JsonProperty("name")
    private String username;
    @JsonProperty("image")
    private String image;

    // this holds the true roles that are available to the user. it can get sent, but it remains unused for the purposes
    // of the front-end, currently.
    @JsonProperty("roles")
    private List<RoleDTO> roles;

    // while this holds the general "type" of role that the user has
    // generally, any user who has a granted authority that is of the form
    // {ELEMENT}_ADMIN is considered to have the roleType ROLE_ADMIN, while anybody else
    // will have the role type ROLE_USER. This is used to exclude users from general admin pages, while actual token
    // checks will be performed server side upon attempting any requests via the backend.
    @JsonProperty("role")
    private String roleType;

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
                ", roleType=" + roleType + '\'' +
                ", account=" + relevantAccount +
                '}';
    }

    /**
     * Using the associated roles (which should be set already), attempts to return the desired enumeration constant
     * that would be associated with users given the set of authorities the user has.
     */
    public void setRoleTypeImplicitly() {
        this.roleType = getRoles().stream().anyMatch(roleDTO -> roleDTO.getAuthority().endsWith("_ADMIN")) ? "ROLE_ADMIN" : "ROLE_USER";
    }
}
