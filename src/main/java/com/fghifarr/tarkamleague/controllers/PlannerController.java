package com.fghifarr.tarkamleague.controllers;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.entities.*;
import com.fghifarr.tarkamleague.models.responses.PlannerSolutionResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.MatchClubRepository;
import com.fghifarr.tarkamleague.repositories.MatchRepository;
import com.fghifarr.tarkamleague.services.PlannerService;
import com.fghifarr.tarkamleague.services.UserService;
import com.fghifarr.tarkamleague.utils.SeasonUtil;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/planner")
public class PlannerController {

    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchClubRepository matchClubRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PlannerService plannerService;

    @GetMapping("/solution")
    public @ResponseBody
    PlannerSolutionResp getPlannerSolution() {
        return plannerService.getSolution();
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @PostMapping("/solve-and-listen")
    public @ResponseBody
    String solveAndListen() {
        if (plannerService.getSolverStatus() == SolverStatus.SOLVING_ACTIVE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solver already running!");

        plannerService.solveAndListen();
        return "Start assigning clubs onto matches";
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @PostMapping("/stop-solving")
    public @ResponseBody
    String stopSolving() {
        if (plannerService.getSolverStatus() != SolverStatus.SOLVING_ACTIVE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solver already stop running!");

        plannerService.stopSolving();
        return "Stop solving!";
    }

    //This should be called whenever user created a new season or try to populate an empty season.
    //Will be implemented in the future updates.
    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR_CREATOR)
    @PostMapping("/matches")
    public @ResponseBody
    String generateMatches() {
        List<Match> matchList = matchRepository.findAll();
        if (!matchList.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can't generate matches if matches already exists!");

        List<Club> participants = clubRepository.findAll();
        int totalParticipants = participants.size();
        if (totalParticipants < 4)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can't generate matches if total participants is less than 4");

        int totalMatches = SeasonUtil.countTotalMatches(totalParticipants);
        int totalGameweeks = SeasonUtil.countTotalGameweeks(totalParticipants);
        User creator = userService.getCurrentUser();

        for (int gameweek = 1; gameweek <= totalGameweeks; gameweek++) {
            LocalDate kickOffDate = LocalDate.now().plusDays(gameweek * 7L);
            LocalTime kickOffTime = LocalTime.of(19, 30, 0);
            LocalDateTime kickOff = LocalDateTime.of(kickOffDate, kickOffTime);

            for (int i = 0; i < totalMatches/totalGameweeks; i++) {
                Match match = Match.builder()
                        .kickOff(kickOff)
                        .gameweek(gameweek)
                        .build();
                match.setCreatedBy(creator);
                match.setModifiedBy(creator);
                matchRepository.saveAndFlush(match);

                MatchHost matchHost = new MatchHost();
                MatchVisitor matchVisitor = new MatchVisitor();
                matchClubRepository.save(matchHost);
                matchClubRepository.save(matchVisitor);
                match.setHost(matchHost);
                match.setVisitor(matchVisitor);
                matchRepository.saveAndFlush(match);
            }
        }

        return "Successfully generated the matches!";
    }

    @PreAuthorize(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @DeleteMapping("/matches")
    public @ResponseBody
    String deleteMatches() {
        if (matchRepository.findAll().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There's no data to be deleted!");

        matchRepository.deleteAll();
        matchClubRepository.deleteAll();

        return "Successfully deleted all the matches!";
    }
}
