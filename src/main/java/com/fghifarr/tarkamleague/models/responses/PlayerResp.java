package com.fghifarr.tarkamleague.models.responses;

import com.fghifarr.tarkamleague.entities.Player;
import lombok.Value;

@Value
public class PlayerResp {
    Long id;
    String name;
    String club;

    public PlayerResp(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.club = player.getClubName();
    }

    public PlayerResp(Long id, String name) {
        this.id = id;
        this.name = name;
        this.club = "Free Agent";
    }

    public PlayerResp(Long id, String name, String club) {
        this.id = id;
        this.name = name;
        if (club == null) this.club = "Free Agent";
        else this.club = club;
    }
}
