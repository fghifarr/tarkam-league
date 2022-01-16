package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.entities.SeasonClub;
import com.fghifarr.tarkamleague.repositories.SeasonClubRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SeasonClubService {

    private final SeasonClubRepository seasonClubRepository;

    public SeasonClubService(SeasonClubRepository seasonClubRepository) {
        this.seasonClubRepository = seasonClubRepository;
    }

    public SeasonClub build(Season season, Club club) {
        if (season.getId() != null && club.getId() != null &&
                seasonClubRepository.existsSeasonClubBySeasonAndClub(season, club))
            return seasonClubRepository.findBySeasonAndClub(season, club);

        SeasonClub seasonClub = new SeasonClub();
        seasonClub.setSeason(season);
        seasonClub.setClub(club);
        club.getSeasons().add(seasonClub);
        season.getClubs().add(seasonClub);

        return seasonClub;
    }
}
