package com.fghifarr.tarkamleague.configs;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.configs.constants.RoleGroupConstant;
import com.fghifarr.tarkamleague.entities.*;
import com.fghifarr.tarkamleague.models.requests.init.InitPlayerReq;
import com.fghifarr.tarkamleague.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Configuration
public class InitData {
    private static final Logger log = LoggerFactory.getLogger(InitData.class);

    @Value("${spring.jpa.hibernate.ddl-auto}")
    String ddlAuto;

    @Bean
    CommandLineRunner initRole(RoleRepository roleRepository) {
        return args -> {
            if (!Objects.equals(ddlAuto, "create-drop"))
                return;

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
            if (!Objects.equals(ddlAuto, "create-drop"))
                return;

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
    CommandLineRunner initUser(RoleGroupRepository roleGroupRepository,
                               UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (!Objects.equals(ddlAuto, "create-drop"))
                return;

            RoleGroup admin = roleGroupRepository.findByName(RoleGroupConstant.ADMIN);
            RoleGroup dataEntry = roleGroupRepository.findByName(RoleGroupConstant.DATA_ENTRY);
            RoleGroup visitor = roleGroupRepository.findByName(RoleGroupConstant.VISITOR);

            List<User> userList = List.of(
                    new User("admin1", encoder.encode("admin1Pass"), admin),
                    new User("admin2", encoder.encode("admin2Pass"), admin),
                    new User("dataEntry1", encoder.encode("dataEntry1Pass"), dataEntry),
                    new User("dataEntry2", encoder.encode("dataEntry1Pass"), dataEntry),
                    new User("visitor1", encoder.encode("visitor1Pass"), visitor),
                    new User("visitor2", encoder.encode("visitor2Pass"), visitor)
            );

            for (User user : userList) {
                log.info("Preloading User: " + userRepository.saveAndFlush(user).getUsername());
            }
        };
    }

    @Bean
    CommandLineRunner initClub(ClubRepository clubRepository) {
        return args -> {
            if (!Objects.equals(ddlAuto, "create-drop"))
                return;

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
    CommandLineRunner initPlayer(ClubRepository clubRepository, PersonalDetailsRepository personalDetailsRepository) {
        return args -> {
            if (!Objects.equals(ddlAuto, "create-drop"))
                return;

            List<InitPlayerReq> playerReqs = List.of(
                    new InitPlayerReq("Steven Gerrard", Player.Position.MIDFIELDER, "", Date.valueOf("1980-05-30"), "England", 185),
                    new InitPlayerReq("Wayne Rooney", Player.Position.FORWARD, "", Date.valueOf("1985-10-24"), "England", 176),
                    new InitPlayerReq("Mohamed Salah", Player.Position.FORWARD, "Liverpool", Date.valueOf("1992-06-15"), "Egypt", 175),
                    new InitPlayerReq("Trent Alexander-Arnold", Player.Position.DEFENDER, "Liverpool", Date.valueOf("1998-10-07"), "England", 175),
                    new InitPlayerReq("Andrew Robertson", Player.Position.DEFENDER, "Liverpool", Date.valueOf("1994-03-11"), "Scotland", 178),
                    new InitPlayerReq("David de Gea", Player.Position.GOALKEEPER, "Manchester United", Date.valueOf("1990-11-07"), "Spain", 192),
                    new InitPlayerReq("Kevin De Bruyne", Player.Position.MIDFIELDER, "Manchester City", Date.valueOf("1991-06-28"), "Belgium", 181)
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

                log.info("Preloading Player: " + personalDetailsRepository.saveAndFlush(profile).getPlayer().getName());
            }
        };
    }
}
