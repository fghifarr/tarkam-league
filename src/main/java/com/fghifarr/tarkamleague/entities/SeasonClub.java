package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "season_club")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeasonClub {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private SeasonClubId id = new SeasonClubId();

    @ManyToOne
    @MapsId("seasonId")
    @JoinColumn(name = "season_id")
    Season season;

    @ManyToOne
    @MapsId("clubId")
    @JoinColumn(name = "club_id")
    Club club;
}
