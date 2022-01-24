package com.fghifarr.tarkamleague.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FixtureResp {

    private LocalDate date;
    private LocalTime time;
    private String host;
    private String visitor;

    public FixtureResp(LocalDateTime dateTime, String host, String visitor) {
        this.date = dateTime.toLocalDate();
        this.time = dateTime.toLocalTime();
        this.host = host;
        this.visitor = visitor;
    }
}
