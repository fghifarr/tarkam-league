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
                new Club("Liverpool"),
                new Club("Manchester City"),
                new Club("Manchester United"),
                new Club("Newcastle United"),
                new Club("West Ham United"),
                new Club("Leicester City")
        );

        for (Club club : clubList) {
            clubRepository.saveAndFlush(club);
            counter++;
        }

        return counter;
    }
}
