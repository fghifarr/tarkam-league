package com.fghifarr.tarkamleague.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerReq {
    @NotBlank(message = "Name cannot be blank!")
    private String name;
    private Long club;
}
