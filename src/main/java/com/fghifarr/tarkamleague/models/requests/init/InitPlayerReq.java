package com.fghifarr.tarkamleague.models.requests.init;

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
public class InitPlayerReq {
    private String name;
    private Player.Position position;

    //club
    private String club;

    //profile
    private Date dob;
    private String nationality;
    private Integer height;
}
