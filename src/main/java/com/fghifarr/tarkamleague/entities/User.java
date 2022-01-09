package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @NotBlank(message = "Name cannot be blank!")
    private String username;
    @NotBlank(message = "Password cannot be blank!")
    private String password;

    @ManyToOne
    @NotNull(message = "Role cannot be null!")
    private Role role;

    public User(Long id, String username, String password, Role role) {
        this(username, password, role);
        this.setId(id);
    }
}
