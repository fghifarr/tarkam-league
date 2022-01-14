package com.fghifarr.tarkamleague.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeasonReq {
    @NotBlank(message = "Name cannot be blank!")
    private String name;

    @NotNull(message = "Clubs cannot be null!")
    @Size(min = 3, max = 25, message = "Total clubs must be between 3 and 25!")
    private Set<Long> clubs;
}
