package com.fghifarr.tarkamleague.configs.planner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchPlanner {
    private Long id;
    private LocalDateTime kickOff;
    private Long hostId;
    private Long hostClubId;
    private String hostName;
    private Long visitorId;
    private Long visitorClubId;
    private String visitorName;
}
