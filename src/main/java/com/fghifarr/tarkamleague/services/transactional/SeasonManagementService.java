package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.entities.SeasonClub;
import com.fghifarr.tarkamleague.models.requests.SeasonReq;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.SeasonClubRepository;
import com.fghifarr.tarkamleague.repositories.SeasonRepository;
import com.fghifarr.tarkamleague.services.SeasonClubService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SeasonManagementService {

    private final SeasonRepository seasonRepository;
    private final ClubRepository clubRepository;
    private final SeasonClubRepository seasonClubRepository;
    private final SeasonClubService seasonClubService;

    public SeasonManagementService(SeasonRepository seasonRepository,
                                   ClubRepository clubRepository,
                                   SeasonClubRepository seasonClubRepository,
                                   SeasonClubService seasonClubService) {
        this.seasonRepository = seasonRepository;
        this.clubRepository = clubRepository;
        this.seasonClubRepository = seasonClubRepository;
        this.seasonClubService = seasonClubService;
    }

    public SeasonResp create(SeasonReq seasonReq) {
        Season season = new Season(seasonReq.getName());
        List<SeasonClub> seasonClubList = new ArrayList<>();
        for (Long clubId : seasonReq.getClubs()) {
            Optional<Club> club = clubRepository.findById(clubId);
            if (club.isEmpty()) continue;

            SeasonClub seasonClub = seasonClubService.build(season, club.get());
            seasonClubList.add(seasonClub);
        }
        seasonRepository.save(season);
        seasonClubRepository.saveAll(seasonClubList);

        return new SeasonResp(season);
    }

    //ToDo: Increase readability
    public SeasonResp update(Long id, SeasonReq seasonReq) {
        Optional<Season> seasonOpt = seasonRepository.findById(id);
        if (seasonOpt.isEmpty()) return null;
        Season season = seasonOpt.get();

        //Load all the clubs from request
        Set<Club> newClubSet = new HashSet<>();
        for (Long clubId : seasonReq.getClubs()) {
            Optional<Club> clubOpt = clubRepository.findById(clubId);
            clubOpt.ifPresent(newClubSet::add);
        }

        //Remove the complement of new club set
        List<SeasonClub> currClubsOfSeason = seasonClubRepository.findAllBySeason(season);
        for (SeasonClub currSeasonClub : currClubsOfSeason) {
            if (!newClubSet.contains(currSeasonClub.getClub()))
                seasonClubRepository.delete(currSeasonClub);
        }

        Set<SeasonClub> newClubsOfSeason = getNewClubsOfSeason(season, newClubSet);
        if (newClubsOfSeason.size() < 3) return null;

        seasonClubRepository.saveAll(newClubsOfSeason);
        season.setName(seasonReq.getName());
        season.setClubs(newClubsOfSeason);
        seasonRepository.save(season);

        return new SeasonResp(season);
    }

    private Set<SeasonClub> getNewClubsOfSeason(Season season, Set<Club> newClubSet) {
        Set<SeasonClub> newClubsOfSeason = new HashSet<>();
        for (Club club : newClubSet) {
            SeasonClub seasonClub;
            if (seasonClubRepository.existsSeasonClubBySeasonAndClub(season, club)) {
                seasonClub = seasonClubRepository.findBySeasonAndClub(season, club);
                newClubsOfSeason.add(seasonClub);
            } else {
                seasonClub = new SeasonClub();
                seasonClub.setSeason(season);
                seasonClub.setClub(club);
                club.getSeasons().add(seasonClub);
                season.getClubs().add(seasonClub);
            }
            newClubsOfSeason.add(seasonClub);
        }

        return newClubsOfSeason;
    }

    public SeasonResp delete(Long id) {
        Optional<Season> seasonOpt = seasonRepository.findById(id);
        if (seasonOpt.isEmpty()) return null;

        Season season = seasonOpt.get();
        seasonRepository.delete(season);

        return new SeasonResp(season);
    }
}
