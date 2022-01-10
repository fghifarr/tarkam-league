package com.fghifarr.tarkamleague.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResp {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;
}
