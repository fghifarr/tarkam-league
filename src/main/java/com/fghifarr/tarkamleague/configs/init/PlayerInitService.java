package com.fghifarr.tarkamleague.configs.init;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.PersonalDetails;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.requests.init.InitPlayerReq;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PersonalDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PlayerInitService {

    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private PersonalDetailsRepository personalDetailsRepository;

    int init() {
        int counter = 0;

        List<InitPlayerReq> playerReqs = List.of(
                new InitPlayerReq("Steven Gerrard", Player.Position.MIDFIELDER, "", LocalDate.parse("1980-05-30"), "England", 185),
                new InitPlayerReq("Wayne Rooney", Player.Position.FORWARD, "", LocalDate.parse("1985-10-24"), "England", 176),
                new InitPlayerReq("Mohamed Salah", Player.Position.FORWARD, "Liverpool", LocalDate.parse("1992-06-15"), "Egypt", 175),
                new InitPlayerReq("Trent Alexander-Arnold", Player.Position.DEFENDER, "Liverpool", LocalDate.parse("1998-10-07"), "England", 175),
                new InitPlayerReq("Andrew Robertson", Player.Position.DEFENDER, "Liverpool", LocalDate.parse("1994-03-11"), "Scotland", 178),
                new InitPlayerReq("David de Gea", Player.Position.GOALKEEPER, "Manchester United", LocalDate.parse("1990-11-07"), "Spain", 192),
                new InitPlayerReq("Kevin De Bruyne", Player.Position.MIDFIELDER, "Manchester City", LocalDate.parse("1991-06-28"), "Belgium", 181)
        );

        for (InitPlayerReq playerReq : playerReqs) {
            Club club = clubRepository.findByName(playerReq.getClub());
            Player player = Player.builder()
                    .name(playerReq.getName())
                    .position(playerReq.getPosition())
                    .club(club)
                    .build();
            PersonalDetails profile = PersonalDetails.builder()
                    .dob(playerReq.getDob())
                    .nationality(playerReq.getNationality())
                    .height(playerReq.getHeight())
                    .player(player)
                    .build();

            personalDetailsRepository.saveAndFlush(profile);
            counter++;
        }

        return counter;
    }
}
