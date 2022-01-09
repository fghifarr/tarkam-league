package com.fghifarr.tarkamleague.startup;

import com.fghifarr.tarkamleague.models.requests.ClubReq;
import com.fghifarr.tarkamleague.models.requests.PlayerReq;
import com.fghifarr.tarkamleague.services.ClubManagementService;
import com.fghifarr.tarkamleague.services.transactional.PlayerManagementService;
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
    CommandLineRunner initClub(ClubManagementService clubManagementService) {
        List<ClubReq> clubList = List.of(
                new ClubReq("Liverpool"),
                new ClubReq("Manchester City"),
                new ClubReq("Manchester United"),
                new ClubReq("Newcastle United"),
                new ClubReq("West Ham United"),
                new ClubReq("Leicester City")
        );

        return args -> {
            for (ClubReq club : clubList) {
                log.info("Preloading Club: " + clubManagementService.create(club).getName());
            }
        };
    }

    @Bean
    CommandLineRunner initPlayer(PlayerManagementService playerManagementService) {
        List<PlayerReq> playerList = List.of(
                new PlayerReq("Steven Gerrard"),
                new PlayerReq("Wayne Rooney"),
                new PlayerReq("Mohammed Salah", 1L),
                new PlayerReq("Trent Alexander-Arnold", 1L),
                new PlayerReq("Andrew Robertson", 1L),
                new PlayerReq("David De Gea", 3L),
                new PlayerReq("Kevin De Bruyne", 2L)
        );

        return args -> {
            for (PlayerReq player : playerList) {
                log.info("Preloading Player: " + playerManagementService.create(player).getName());
            }
        };
    }
}
