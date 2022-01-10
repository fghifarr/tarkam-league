package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "role")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue
    private Long id;

    @NotBlank(message = "Name cannot be blank!")
    private String name;

    public Role(String name) {
        this.name = name;
    }
}
