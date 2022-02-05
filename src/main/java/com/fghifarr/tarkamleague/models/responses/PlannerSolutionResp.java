package com.fghifarr.tarkamleague.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlannerSolutionResp {

    private HardSoftScore score;
    private Map<LocalDateTime, List<String>> fixturesPerGameweek = new HashMap<>();
}
