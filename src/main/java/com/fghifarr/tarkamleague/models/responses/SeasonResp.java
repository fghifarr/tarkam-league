package com.fghifarr.tarkamleague.models.responses;

import com.fghifarr.tarkamleague.entities.Season;
import lombok.Value;

@Value
public class SeasonResp {
    Long id;
    String name;

    public SeasonResp(Season season) {
        this.id = season.getId();
        this.name = season.getName();
    }

    public SeasonResp(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
