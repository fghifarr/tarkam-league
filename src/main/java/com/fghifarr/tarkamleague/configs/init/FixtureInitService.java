package com.fghifarr.tarkamleague.configs.init;

import com.fghifarr.tarkamleague.entities.*;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.MatchClubRepository;
import com.fghifarr.tarkamleague.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
public class FixtureInitService {

    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchClubRepository matchClubRepository;

    int init() {
        int counter = 0;

        List<Club> participants = clubRepository.findAll();
        //Total participants must be even for this data generator
        if (participants.size() % 2 != 0) return counter;

        int totalHalfGameweeks = participants.size() - 1;
        Map<Club, boolean[]> clubAvailability = new HashMap<>();
        LinkedList<List<Club>> clubPairs = new LinkedList<>();
        for (int i = 0; i < participants.size() - 1; i++) {
            for (int j = i + 1; j < participants.size(); j++) {
                clubPairs.add(List.of(participants.get(i), participants.get(j)));
            }
        }

        for (Club club : participants) {
            clubAvailability.put(club, new boolean[totalHalfGameweeks]);
        }

        while (!clubPairs.isEmpty()) {
            List<Club> clubMatchPair = clubPairs.remove();
            Club homeClub = clubMatchPair.get(0);
            Club awayClub = clubMatchPair.get(1);

            for (int i = 0; i < totalHalfGameweeks; i++) {
                if (!clubAvailability.get(homeClub)[i] && !clubAvailability.get(awayClub)[i]) {
                    clubAvailability.get(homeClub)[i] = true;
                    clubAvailability.get(awayClub)[i] = true;
                    populateMatch(homeClub, awayClub, i*2 + 1);
                    populateMatch(awayClub, homeClub, i*2 + 2);
                    counter += 2;
                    break;
                }
            }
        }

        return counter;
    }

    void populateMatch(Club homeClub, Club awayClub, int gameweek) {
        LocalDate kickOffDate = LocalDate.now().plusDays(gameweek * 7L);
        LocalTime kickOffTime = LocalTime.of(19, 30, 0);
        LocalDateTime kickOff = LocalDateTime.of(kickOffDate, kickOffTime);

        Match match = Match.builder()
                .kickOff(kickOff)
                .build();
        matchRepository.saveAndFlush(match);

        MatchHost matchHost = new MatchHost();
        matchHost.setClub(homeClub);

        MatchVisitor matchVisitor = new MatchVisitor();
        matchVisitor.setClub(awayClub);

        matchClubRepository.save(matchHost);
        matchClubRepository.save(matchVisitor);
        match.setHost(matchHost);
        match.setVisitor(matchVisitor);
        matchRepository.saveAndFlush(match);
    }
}
