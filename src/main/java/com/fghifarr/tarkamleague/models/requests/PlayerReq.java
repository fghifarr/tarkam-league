package com.fghifarr.tarkamleague.models.requests;

import com.fghifarr.tarkamleague.entities.Player;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerReq {
    @NotBlank(message = "Name cannot be blank!")
    private String name;
    @NotNull(message = "Position cannot be null!")
    private Player.Position position;

    private Long club;

    @NotNull(message = "Profile cannot be null!")
    private PersonalDetailsReq profile;

    public PlayerReq(String name) {
        this.name = name;
    }
}
