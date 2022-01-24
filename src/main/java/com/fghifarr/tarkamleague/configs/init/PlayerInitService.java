package com.fghifarr.tarkamleague.configs.init;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.PersonalDetails;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PersonalDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PlayerInitService {

    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private PersonalDetailsRepository personalDetailsRepository;

    int init() {
        int counter = 0;

        List<Club> clubList = clubRepository.findAll();
        Map<Player.Position, Integer> totalPlayerForEachPosition = Map.of(
                Player.Position.GOALKEEPER, 3,
                Player.Position.DEFENDER, 7,
                Player.Position.MIDFIELDER, 7,
                Player.Position.FORWARD, 5
        );

        for (Club club : clubList) {
            for (Map.Entry<Player.Position, Integer> entry : totalPlayerForEachPosition.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    generatePlayer(club, entry.getKey());
                    counter++;
                }
            }
        }

        return counter;
    }

    void generatePlayer(Club club, Player.Position position) {
        String playerName = DummyData.generateDummyName();
        LocalDate dob = DummyData.generateDOB();
        String nationality = DummyData.generateNationality();
        int height = DummyData.generateHeight();

        Player player = Player.builder()
                .name(playerName)
                .position(position)
                .club(club)
                .build();
        PersonalDetails profile = PersonalDetails.builder()
                .dob(dob)
                .nationality(nationality)
                .height(height)
                .player(player)
                .build();

        personalDetailsRepository.saveAndFlush(profile);
    }
}
