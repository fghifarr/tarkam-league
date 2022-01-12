package com.fghifarr.tarkamleague.models.responses;

import com.fghifarr.tarkamleague.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDetailsResp {
    private Long id;
    private String name;
    private String club;
    private PlayerProfileResp profile;

    public PlayerDetailsResp(Long id, String name, String club, Date dob, String nationality, Integer height) {
        this.id = id;
        this.name = name;
        this.club = club;
        this.profile = new PlayerProfileResp(dob, nationality, height);
    }

    public PlayerDetailsResp(Long id, String name, String club, java.util.Date utilDob, String nationality, Integer height) {
        Date dob = new Date(utilDob.getTime());

        this.id = id;
        this.name = name;
        this.club = club;
        this.profile = new PlayerProfileResp(dob, nationality, height);
    }

    public PlayerDetailsResp(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.club = player.getClubName();
        this.profile = new PlayerProfileResp(player.getProfile());
    }
}
