package com.fghifarr.tarkamleague.configs;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.configs.constants.RoleGroupConstant;
import com.fghifarr.tarkamleague.entities.*;
import com.fghifarr.tarkamleague.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
public class InitData {
    private static final Logger log = LoggerFactory.getLogger(InitData.class);

    @Bean
    CommandLineRunner initRole(RoleRepository roleRepository) {
        return args -> {
            List<Role> roleList = List.of(
                    new Role(RoleConstant.ADMINISTRATOR),
                    new Role(RoleConstant.CREATOR),
                    new Role(RoleConstant.EDITOR),
                    new Role(RoleConstant.VIEWER)
            );

            for (Role role : roleList) {
                log.info("Preloading Role: " + roleRepository.saveAndFlush(role).getName());
            }
        };
    }

    @Bean
    CommandLineRunner initRoleGroup(RoleRepository roleRepository, RoleGroupRepository roleGroupRepository) {
        return args -> {
            Role administrator = roleRepository.findByName(RoleConstant.ADMINISTRATOR);
            Role creator = roleRepository.findByName(RoleConstant.CREATOR);
            Role editor = roleRepository.findByName(RoleConstant.EDITOR);
            Role viewer = roleRepository.findByName(RoleConstant.VIEWER);

            List<RoleGroup> roleGroupList = List.of(
                    new RoleGroup(RoleGroupConstant.ADMIN, Set.of(administrator)),
                    new RoleGroup(RoleGroupConstant.DATA_ENTRY, Set.of(creator, editor, viewer)),
                    new RoleGroup(RoleGroupConstant.VISITOR, Set.of(viewer))
            );

            for (RoleGroup roleGroup : roleGroupList) {
                log.info("Preloading Role Group: " + roleGroupRepository.saveAndFlush(roleGroup).getName());
            }
        };
    }

    @Bean
    CommandLineRunner initUser(RoleGroupRepository roleGroupRepository, UserRepository userRepository) {
        return args -> {
            RoleGroup admin = roleGroupRepository.findByName(RoleGroupConstant.ADMIN);
            RoleGroup dataEntry = roleGroupRepository.findByName(RoleGroupConstant.DATA_ENTRY);
            RoleGroup visitor = roleGroupRepository.findByName(RoleGroupConstant.VISITOR);

            List<User> userList = List.of(
                    new User("admin1", "admin1Pass", admin),
                    new User("admin2", "admin2Pass", admin),
                    new User("dataEntry1", "dataEntry1Pass", dataEntry),
                    new User("dataEntry2", "dataEntry1Pass", dataEntry),
                    new User("visitor1", "visitor1Pass", visitor),
                    new User("visitor2", "visitor2Pass", visitor)
            );

            for (User user : userList) {
                log.info("Preloading User: " + userRepository.saveAndFlush(user).getUsername());
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
