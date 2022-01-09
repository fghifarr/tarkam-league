package com.fghifarr.tarkamleague.startup;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InitData {
    private static final Logger log = LoggerFactory.getLogger(InitData.class);

    @Bean
    CommandLineRunner initClub(ClubRepository clubRepository) {
        return args -> {
            List<Club> clubList = List.of(
                    new Club("Liverpool"),
                    new Club("Manchester City"),
                    new Club("Manchester United"),
                    new Club("Newcastle United"),
                    new Club("West Ham United"),
                    new Club("Leicester City")
            );

            for (Club club : clubList) {
                log.info("Preloading Club: " + clubRepository.saveAndFlush(club).getName());
            }
        };
    }

    @Bean
    CommandLineRunner initPlayer(PlayerRepository playerRepository, ClubRepository clubRepository) {
        return args -> {
            Club liverpool = clubRepository.findByName("Liverpool");
            Club manUtd = clubRepository.findByName("Manchester United");
            Club manCity = clubRepository.findByName("Manchester City");

            List<Player> playerList = List.of(
                    new Player("Steven Gerrard"),
                    new Player("Wayne Rooney"),
                    new Player("Mohammed Salah", liverpool),
                    new Player("Trent Alexander-Arnold", liverpool),
                    new Player("Andrew Robertson", liverpool),
                    new Player("David De Gea", manUtd),
                    new Player("Kevin De Bruyne", manCity)
            );

            for (Player player : playerList) {
                log.info("Preloading Player: " + playerRepository.saveAndFlush(player).getName());
            }
        };
    }
}
