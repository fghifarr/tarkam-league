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
                oneClubAtAMatchRule(constraintFactory),
                uniqueOpponentOnEachSideRule(constraintFactory)
        };
    }

    private Constraint oneClubAtAGameweekRule(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(MatchClubPlanner.class)
                .join(MatchClubPlanner.class,
                        Joiners.equal(MatchClubPlanner::getClub),
                        Joiners.equal(MatchClubPlanner::getGameweek))
                .filter((mc1, mc2) -> mc1 != mc2)
                .penalize("Club can't play for more than one match on a gameweek", HardSoftScore.ofHard(100));
    }

    private Constraint oneClubAtAMatchRule(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(MatchClubPlanner.class)
                .join(MatchClubPlanner.class,
                        Joiners.equal(MatchClubPlanner::getClub),
                        Joiners.equal(MatchClubPlanner::getMatchId))
                .filter((mc1, mc2) -> mc1 != mc2)
                .penalize("Club can't play for the same match", HardSoftScore.ofHard(10000));
    }

    private Constraint uniqueOpponentOnEachSideRule(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(MatchClubPlanner.class) //Club A, Gameweek 1 (A1)
                .join(MatchClubPlanner.class, //Club X => same match with A1
                        Joiners.equal(MatchClubPlanner::getMatchId),
                        Joiners.filtering((mc1, mc2) -> !mc1.getClub().equals(mc2.getClub())))
                .join(MatchClubPlanner.class, //Club A, gameweek 2 (A2) => same side with A1
                        Joiners.equal((mc1, s2) -> mc1.getClub(), MatchClubPlanner::getClub),
                        Joiners.equal((mc1, s2) -> mc1.getSide(), MatchClubPlanner::getSide),
                        Joiners.filtering((mc1, mc2, mc3) -> !mc1.getGameweek().equals(mc3.getGameweek())))
                .join(MatchClubPlanner.class, //Club Y => same match with A2
                        Joiners.equal((mc1, mc2, mc3) -> mc3.getMatchId(), MatchClubPlanner::getMatchId),
                        Joiners.filtering((mc1, mc2, mc3, mc4) -> !mc3.getClub().equals(mc4.getClub())))
                .filter((mc1, mc2, mc3, mc4) -> mc2.getClub() == mc4.getClub())
                .penalize("Club opponent must be unique on each side", HardSoftScore.ofHard(1));
    }
}
