package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.configs.planner.*;
import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Match;
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

        for (MatchPlannerV2 matchPlanner : solution.getMatchList()) {
            if (!resp.getFixturesPerGameweek().containsKey(matchPlanner.getGameweek()))
                resp.getFixturesPerGameweek().put(matchPlanner.getGameweek(), new ArrayList<>());

            resp.getFixturesPerGameweek().get(matchPlanner.getGameweek()).add(matchPlanner.toString());
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
        for (MatchPlanner matchDto : matchList) {
            Optional<ClubPlanner> hostClubOpt = clubList.stream()
                    .filter(it -> Objects.equals(it.getId(), matchDto.getHostClubId()))
                    .findFirst();
            Optional<ClubPlanner> visitorClubOpt = clubList.stream()
                    .filter(it -> Objects.equals(it.getId(), matchDto.getVisitorClubId()))
                    .findFirst();

            MatchPlannerV2 match = MatchPlannerV2.builder()
                    .id(matchDto.getId())
                    .gameweek(matchDto.getGameweek())
                    .kickOff(matchDto.getKickOff())
                    .host(hostClubOpt.orElse(null))
                    .visitor(visitorClubOpt.orElse(null))
                    .build();
            solution.getMatchList().add(match);
        }

        scoreManager.updateScore(solution);

        return solution;
    }

    private void updateFixtures(PlannerSolution plannerSolution) {
        for (MatchPlannerV2 matchPlanner : plannerSolution.getMatchList()) {
            Optional<Match> matchOpt = matchRepository.findById(matchPlanner.getId());
            if (matchOpt.isEmpty()) return;
            Match match = matchOpt.get();

            Optional<Club> hostOpt = Optional.empty();
            if (matchPlanner.getHost() != null)
                hostOpt = clubRepository.findById(matchPlanner.getHostId());
            Club host = hostOpt.orElse(null);
            match.getHost().setClub(host);
            matchClubRepository.save(match.getHost());

            Optional<Club> visitorOpt = Optional.empty();
            if (matchPlanner.getVisitor() != null)
                visitorOpt = clubRepository.findById(matchPlanner.getVisitorId());
            Club visitor = visitorOpt.orElse(null);
            match.getVisitor().setClub(visitor);
            matchClubRepository.save(match.getVisitor());
        }
    }
}
