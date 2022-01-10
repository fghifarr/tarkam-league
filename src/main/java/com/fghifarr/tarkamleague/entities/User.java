package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "user")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue
    private Long id;

    @NotBlank(message = "Name cannot be blank!")
    private String username;
    @NotBlank(message = "Password cannot be blank!")
    private String password;

    @ManyToOne @JoinColumn(name = "role_group_id")
    @NotNull(message = "Role group cannot be null!")
    private RoleGroup roleGroup;

    private Date dateCreated = new Date();
    private Date lastUpdated = new Date();

    public User(String username, String password, RoleGroup roleGroup) {
        this.username = username;
        this.password = password;
        this.roleGroup = roleGroup;
    }
}
