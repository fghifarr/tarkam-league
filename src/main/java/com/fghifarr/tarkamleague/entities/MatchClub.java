package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Inheritance
@DiscriminatorColumn(name="role")
@Table(name = "match_club")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchClub extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "matchClub")
    private Set<MatchIndividualEvent> individualEvents;
}
