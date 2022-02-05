package com.fghifarr.tarkamleague.configs.planner;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class PlannerConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                oneClubAtAGameweekRule(constraintFactory),
                oneClubAtAMatchRule(constraintFactory)
        };
    }

    private Constraint oneClubAtAGameweekRule(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(MatchClubPlanner.class)
                .join(MatchClubPlanner.class,
                        Joiners.equal(MatchClubPlanner::getClub),
                        Joiners.equal(MatchClubPlanner::getGameweek)
                ).filter((s1, s2) -> s1 != s2)
                .penalize("Club can't play for more than one match on a gameweek", HardSoftScore.ofHard(50));
    }

    private Constraint oneClubAtAMatchRule(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(MatchClubPlanner.class)
                .join(MatchClubPlanner.class,
                        Joiners.equal(MatchClubPlanner::getClub),
                        Joiners.equal(MatchClubPlanner::getMatchId)
                ).filter((s1, s2) -> s1 != s2)
                .penalize("Club can't play for the same match", HardSoftScore.ofHard(100));
    }
}
