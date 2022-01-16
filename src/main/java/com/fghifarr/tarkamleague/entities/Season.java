package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "season")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Season extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Name cannot be blank!")
    private String name;

    @OneToMany(mappedBy = "season")
    @NotNull(message = "Clubs cannot be null!")
    @Size(min = 3, max = 25, message = "Total clubs must be between 3 and 25!")
    private Set<SeasonClub> clubs = new HashSet<>();

    public Season(String name) {
        this.name = name;
    }

    public Season(Long id, String name) {
        this(name);
        this.setId(id);
    }
}
