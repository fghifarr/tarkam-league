package com.fghifarr.tarkamleague.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeasonClubResp {
    private Long seasonId;
    private String season;
    private String club;
}
