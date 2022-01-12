package com.fghifarr.tarkamleague.models.responses;

import com.fghifarr.tarkamleague.entities.PersonalDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfileResp {
    private Date dob;
    private String nationality;
    private Integer height;

    public PlayerProfileResp(PersonalDetails profile) {
        this.dob = profile.getDob();
        this.nationality = profile.getNationality();
        this.height = profile.getHeight();
    }
}
