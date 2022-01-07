package com.fghifarr.tarkamleague.models.responses;

import com.fghifarr.tarkamleague.entities.Club;
import lombok.Value;

@Value
public class ClubResp {
    Long id;
    String name;

    public ClubResp(Club club) {
        this.id = club.getId();
        this.name = club.getName();
    }

    public ClubResp(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
