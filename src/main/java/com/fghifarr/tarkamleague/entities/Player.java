package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "player")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player extends BaseEntity {
    @NotBlank(message = "Name cannot be null!")
    private String name;

    @ManyToOne @JoinColumn(name = "club_id")
    private Club club;

    public Player(String name) {
        this.name = name;
    }

    public Player(Long id, String name) {
        this(name);
        this.setId(id);
    }

    public Player(Long id, String name, Club club) {
        this(id, name);
        this.setClub(club);
    }

    public String getClubName() {
        if (this.club == null) {
            return "Free Agent";
        }

        return this.club.getName();
    }
}
