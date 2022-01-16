package com.fghifarr.tarkamleague.models.responses;

import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.entities.SeasonClub;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeasonDetailsResp {
    private Long id;
    private String name;
    private List<String> clubList = new ArrayList<>();

    public SeasonDetailsResp(List<SeasonClubResp> seasonClubRespList) {
        this.id = seasonClubRespList.get(0).getSeasonId();
        this.name = seasonClubRespList.get(0).getSeason();
        for (SeasonClubResp seasonClubResp : seasonClubRespList) {
            this.clubList.add(seasonClubResp.getClub());
        }
    }

    public SeasonDetailsResp(Season season) {
        this.id = season.getId();
        this.name = season.getName();
        for (SeasonClub seasonClub : season.getClubs()) {
            this.clubList.add(seasonClub.getClub().getName());
        }
    }
}
