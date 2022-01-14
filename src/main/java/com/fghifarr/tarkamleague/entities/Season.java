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

    @ManyToMany
    @JoinTable(
            name = "season_club",
            joinColumns = @JoinColumn(name = "season_id"),
            inverseJoinColumns = @JoinColumn(name = "club_id")
    )
    @NotNull(message = "Clubs cannot be null!")
    @Size(min = 3, max = 25, message = "Total clubs must be between 3 and 25!")
    private Set<Club> clubs = new HashSet<>();

    public Season(Long id, String name, Set<Club> clubs) {
        this(name, clubs);
        this.setId(id);
    }
}
