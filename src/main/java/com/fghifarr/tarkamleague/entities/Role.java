package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "role")
@Builder
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseEntity {
    @NotBlank(message = "Name cannot be blank!")
    private String name;

    public Role(String name) {
        this.name = name;
    }

    public Role(Long id, String name) {
        this(name);
        this.setId(id);
    }
}
