package com.fghifarr.tarkamleague.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleGroup {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "role_group_role",
            joinColumns = @JoinColumn(name = "role_group_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public RoleGroup(String name, Set<Role> roles) {
        this.name = name;
        this.roles = roles;
    }
}
