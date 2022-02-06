package com.fghifarr.tarkamleague.configs.planner;

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
@PlanningEntity
public class MatchPlannerV2 {

    @PlanningId
    private Long id;

    private Integer gameweek;
    private LocalDateTime kickOff;

    @PlanningVariable(valueRangeProviderRefs = "clubRange")
    private ClubPlanner host;
    @PlanningVariable(valueRangeProviderRefs = "clubRange")
    private ClubPlanner visitor;

    public String getHostStr() {
        if (this.host == null) return "Empty";

        return this.host.getName();
    }
    public Long getHostId() {
        if (this.host == null) return null;

        return this.host.getId();
    }

    public String getVisitorStr() {
        if (this.visitor == null) return "Empty";

        return this.visitor.getName();
    }
    public Long getVisitorId() {
        if (this.visitor == null) return null;

        return this.visitor.getId();
    }

    public String toString() {
        return getHostStr() + " - " + getVisitorStr();
    }
}
