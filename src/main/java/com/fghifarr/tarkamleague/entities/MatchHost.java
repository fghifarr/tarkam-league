package com.fghifarr.tarkamleague.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@DiscriminatorValue("HOME")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchHost extends MatchClub {

    private static long serialVersionUID = 1L;

    @OneToOne(mappedBy = "host")
    private Match match;
}
