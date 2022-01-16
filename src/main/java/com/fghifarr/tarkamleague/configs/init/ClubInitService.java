package com.fghifarr.tarkamleague.configs.init;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClubInitService {

    @Autowired
    private ClubRepository clubRepository;

    int init() {
        int counter = 0;

        List<Club> clubList = List.of(
                new Club("Manchester City"),
                new Club("Chelsea"),
                new Club("Liverpool"),
                new Club("West Ham United"),
                new Club("Arsenal"),
                new Club("Tottenham Hotspur"),
                new Club("Manchester United"),
                new Club("Wolverhampton Wanderers"),
                new Club("Brighton and Hove Albion"),
                new Club("Leicester City"),
                new Club("Southampton"),
                new Club("Crystal Palace"),
                new Club("Brentford"),
                new Club("Aston Villa"),
                new Club("Everton"),
                new Club("Leeds United"),
                new Club("Watford"),
                new Club("Burnley"),
                new Club("Newcastle United"),
                new Club("Norwich City")
        );

        for (Club club : clubList) {
            clubRepository.saveAndFlush(club);
            counter++;
        }

        return counter;
    }
}
