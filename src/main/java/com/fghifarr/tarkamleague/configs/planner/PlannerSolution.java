package com.fghifarr.tarkamleague.configs.planner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PlanningSolution
public class PlannerSolution {

    private static final long serialVersionUID = 1L;

    @PlanningId
    private Long id;

    @ValueRangeProvider(id = "clubRange")
    @ProblemFactCollectionProperty
    private List<ClubPlanner> clubList = new ArrayList<>();

    @PlanningEntityCollectionProperty
    private List<MatchPlannerV2> matchList = new ArrayList<>();

    @PlanningScore
    private HardSoftScore score;

    private SolverStatus solverStatus;
}
