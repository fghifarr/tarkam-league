package com.fghifarr.tarkamleague.configs.planner;

import com.fghifarr.tarkamleague.configs.constants.MatchSide;
import lombok.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@PlanningEntity
@Deprecated
public class MatchClubPlanner {

//    @PlanningId
    private Long id;

    private Long matchId;

    private Integer gameweek;
    private LocalDateTime kickOff;
    private MatchSide side;

//    @PlanningVariable(valueRangeProviderRefs = "clubRange")
    private ClubPlanner club;

    public String getClubStr() {
        if (this.club == null) return "Empty";

        return this.club.getName();
    }
}
