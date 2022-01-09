package com.fghifarr.tarkamleague.startup;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.entities.Role;
import com.fghifarr.tarkamleague.entities.User;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import com.fghifarr.tarkamleague.repositories.RoleRepository;
import com.fghifarr.tarkamleague.repositories.UserRepository;
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
    CommandLineRunner initRole(RoleRepository roleRepository) {
        return args -> {
            List<Role> roleList = List.of(
                    new Role("Admin"),
                    new Role("Creator"),
                    new Role("Editor"),
                    new Role("Reviewer")
            );

            for (Role role : roleList) {
                log.info("Preloading Role: " + roleRepository.save(role).getName());
            }
        };
    }

    @Bean
    CommandLineRunner initUser(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            Role admin = roleRepository.findByName("Admin");
            Role creator = roleRepository.findByName("Creator");
            Role editor = roleRepository.findByName("Editor");
            Role reviewer = roleRepository.findByName("Reviewer");

            List<User> userList = List.of(
                    new User("admin1", "admin1Pass", admin),
                    new User("admin2", "admin2Pass", admin),
                    new User("creator1", "creator1Pass", creator),
                    new User("editor1", "editor1Pass", editor),
                    new User("reviewer1", "reviewer1Pass", reviewer)
            );

            for (User user : userList) {
                log.info("Preloading User: " + userRepository.save(user).getUsername());
            }
        };
    }

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
