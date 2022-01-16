package com.fghifarr.tarkamleague.configs.init;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.entities.SeasonClub;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.SeasonClubRepository;
import com.fghifarr.tarkamleague.repositories.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class SeasonInitService {

    @Autowired
    private SeasonRepository seasonRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private SeasonClubRepository seasonClubRepository;

    public int init() {
        int counter = 0;

        List<Club> clubList = clubRepository.findAll();
        List<String> seasonNameList = List.of(
                "2021/22", "2020/21", "2019/20", "2018/19", "2017/18"
        );

        for (String seasonName : seasonNameList) {
            Season season = new Season(seasonName);
            List<SeasonClub> seasonClubList = new ArrayList<>();
            Collections.shuffle(clubList);

            for (Club club : clubList.subList(0, clubList.size() - 3)) {
                SeasonClub seasonClub = new SeasonClub();
                seasonClub.setSeason(season);
                seasonClub.setClub(club);
                club.getSeasons().add(seasonClub);
                season.getClubs().add(seasonClub);
                seasonClubList.add(seasonClub);
            }
            seasonRepository.saveAndFlush(season);
            seasonClubRepository.saveAllAndFlush(seasonClubList);
            counter++;
        }

        return counter;
    }
}
