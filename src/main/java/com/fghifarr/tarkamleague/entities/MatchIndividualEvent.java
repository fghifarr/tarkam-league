package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "match_individual_event")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchIndividualEvent extends BaseEntity {

    private static long serialVersionUID = 1L;

    public enum Event {
        GOAL, YELLOW_CARD, RED_CARD, ASSIST, OWN_GOAL
    }

    @ManyToOne
    @JoinColumn(name = "match_club_id")
    private MatchClub matchClub;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private int minute;
    private Event event;
}
