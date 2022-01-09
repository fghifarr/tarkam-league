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
public class RoleGroup extends BaseEntity {
    private String name;

    @ManyToMany
    @JoinTable(
            name = "role_group_role",
            joinColumns = @JoinColumn(name = "role_group_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
