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
                cantPlaysAgainstThemselvesRule(constraintFactory),
                hostingOnceAtAGameweekRule(constraintFactory),
                visitingOnceAtAGameweekRule(constraintFactory),
                uniqueOpponentOnEachSideRule(constraintFactory)
        };
    }

    private Constraint cantPlaysAgainstThemselvesRule(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(MatchPlannerV2.class)
                .filter(m -> m.getHost() == m.getVisitor())
                .penalize("Club can't play against themselves", HardSoftScore.ofHard(1000000));
    }

    private Constraint hostingOnceAtAGameweekRule(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(MatchPlannerV2.class,
                        Joiners.equal(MatchPlannerV2::getHost),
                        Joiners.equal(MatchPlannerV2::getGameweek))
                .penalize("Club can't be a host for more than one match on a gameweek", HardSoftScore.ofHard(10000));
    }

    private Constraint visitingOnceAtAGameweekRule(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(MatchPlannerV2.class,
                        Joiners.equal(MatchPlannerV2::getVisitor),
                        Joiners.equal(MatchPlannerV2::getGameweek))
                .penalize("Club can't be a visitor for more than one match on a gameweek", HardSoftScore.ofHard(10000));
    }

    private Constraint uniqueOpponentOnEachSideRule(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(MatchPlannerV2.class,
                        Joiners.equal(MatchPlannerV2::getHost),
                        Joiners.equal(MatchPlannerV2::getVisitor))
                .penalize("Club opponent must be unique on each side", HardSoftScore.ofHard(1));
    }
}
