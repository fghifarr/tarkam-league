package com.fghifarr.tarkamleague.configs.planner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubPlanner {

    @PlanningId
    private Long id;

    private String name;
}
