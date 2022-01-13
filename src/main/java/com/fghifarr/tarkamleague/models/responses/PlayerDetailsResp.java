package com.fghifarr.tarkamleague.models.responses;

import com.fghifarr.tarkamleague.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDetailsResp {
    private Long id;
    private String name;
    private String club;
    private PlayerProfileResp profile;

    public PlayerDetailsResp(Long id, String name, String club, LocalDate dob, String nationality, Integer height) {
        this.id = id;
        this.name = name;
        this.club = club == null ? "Free Agent" : club;
        this.profile = new PlayerProfileResp(dob, nationality, height);
    }

    public PlayerDetailsResp(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.club = player.getClubName() == null ? "Free Agent" : player.getClubName();
        this.profile = new PlayerProfileResp(player.getProfile());
    }
}
