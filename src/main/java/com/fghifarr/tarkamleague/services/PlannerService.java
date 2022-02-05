package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.configs.constants.MatchSide;
import com.fghifarr.tarkamleague.configs.planner.ClubPlanner;
import com.fghifarr.tarkamleague.configs.planner.MatchClubPlanner;
import com.fghifarr.tarkamleague.configs.planner.MatchPlanner;
import com.fghifarr.tarkamleague.configs.planner.PlannerSolution;
import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.MatchClub;
import com.fghifarr.tarkamleague.models.responses.PlannerSolutionResp;
import com.fghifarr.tarkamleague.repositories.*;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlannerService {

    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchClubRepository matchClubRepository;
    @Autowired
    private SolverManager<PlannerSolution, Long> solverManager;
    @Autowired
    private ScoreManager<PlannerSolution, HardSoftScore> scoreManager;

    private static final Long PROBLEM_ID = 1L;

    public void solveAndListen() {
        solverManager.solveAndListen(
                PROBLEM_ID, this::buildPlannerSolution, this::updateFixtures);
    }

    public SolverStatus getSolverStatus() {
        return solverManager.getSolverStatus(PROBLEM_ID);
    }

    public void stopSolving() {
        solverManager.terminateEarly(PROBLEM_ID);
    }

    public PlannerSolutionResp getSolution() {
        PlannerSolution solution = buildPlannerSolution(PROBLEM_ID);
        PlannerSolutionResp resp = new PlannerSolutionResp();
        Map<Integer, Map<Long, Map<MatchSide, String>>> fixturesPerGameweek = new HashMap<>();

        for (MatchClubPlanner matchClub : solution.getMatchClubList()) {
            fixturesPerGameweek
                    .put(matchClub.getGameweek(), fixturesPerGameweek
                            .getOrDefault(matchClub.getGameweek(), new HashMap<>()));
            fixturesPerGameweek
                    .get(matchClub.getGameweek())
                    .put(matchClub.getMatchId(), fixturesPerGameweek
                            .get(matchClub.getGameweek())
                            .getOrDefault(matchClub.getMatchId(), new HashMap<>()));
            fixturesPerGameweek
                    .get(matchClub.getGameweek())
                    .get(matchClub.getMatchId())
                    .put(matchClub.getSide(), matchClub.getClubStr());

        }
        for (Map.Entry<Integer, Map<Long, Map<MatchSide, String>>> entry : fixturesPerGameweek.entrySet()) {
            Integer gameweek = entry.getKey();
            List<String> matchList = new ArrayList<>();

            for (Map<MatchSide, String> map : entry.getValue().values()) {
                matchList.add(map.get(MatchSide.HOME) + " - " + map.get(MatchSide.AWAY));
            }
            resp.getFixturesPerGameweek().put(gameweek, matchList);
        }
        resp.setScore(solution.getScore());

        return resp;
    }

    public PlannerSolution buildPlannerSolution(Long id) {
        PlannerSolution solution = new PlannerSolution();

        solution.setId(PROBLEM_ID);

        List<ClubPlanner> clubList = clubRepository.findAllClubPlanners();
        solution.getClubList().addAll(clubList);

        List<MatchPlanner> matchList = matchRepository.findAllMatchPlanners();
        for (MatchPlanner match : matchList) {
            Optional<ClubPlanner> hostClubOpt = clubList.stream()
                    .filter(it -> Objects.equals(it.getId(), match.getHostClubId()))
                    .findFirst();
            Optional<ClubPlanner> visitorClubOpt = clubList.stream()
                    .filter(it -> Objects.equals(it.getId(), match.getVisitorClubId()))
                    .findFirst();

            MatchClubPlanner host = MatchClubPlanner.builder()
                    .id(match.getHostId())
                    .matchId(match.getId())
                    .gameweek(match.getGameweek())
                    .kickOff(match.getKickOff())
                    .side(MatchSide.HOME)
                    .club(hostClubOpt.orElse(null))
                    .build();
            solution.getMatchClubList().add(host);

            MatchClubPlanner visitor = MatchClubPlanner.builder()
                    .id(match.getVisitorId())
                    .matchId(match.getId())
                    .gameweek(match.getGameweek())
                    .kickOff(match.getKickOff())
                    .side(MatchSide.AWAY)
                    .club(visitorClubOpt.orElse(null))
                    .build();
            solution.getMatchClubList().add(visitor);
        }

        scoreManager.updateScore(solution);

        return solution;
    }

    private void updateFixtures(PlannerSolution plannerSolution) {
        for (MatchClubPlanner matchClubPlanner : plannerSolution.getMatchClubList()) {
            Optional<MatchClub> matchClubOpt = matchClubRepository.findById(matchClubPlanner.getId());
            if (matchClubOpt.isEmpty()) return;
            MatchClub matchClub = matchClubOpt.get();

            Optional<Club> clubOpt = Optional.empty();
            if (matchClubPlanner.getClub() != null)
                clubOpt = clubRepository.findById(matchClubPlanner.getClub().getId());
            Club club = clubOpt.orElse(null);

            matchClub.setClub(club);
            matchClubRepository.save(matchClub);
        }
    }
}
