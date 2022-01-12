package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.PersonalDetails;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.entities.User;
import com.fghifarr.tarkamleague.models.requests.PlayerReq;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import com.fghifarr.tarkamleague.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration
@WithMockUser(username = "adminTest", roles = RoleConstant.ADMINISTRATOR)
public class PlayerManagementServiceTest {

    @Mock
    PlayerRepository playerRepository;
    @Mock
    ClubRepository clubRepository;
    @Mock
    UserService userService;

    PlayerManagementService playerManagementService;
    @BeforeEach
    void initDependencies() {
        playerManagementService = new PlayerManagementService(
                playerRepository, clubRepository, userService
        );
    }

    //============
    //---CREATE---
    //============
    @Test
    public void create_success() {
        User creator = User.builder()
                .username("adminTest")
                .build();
        Club club = new Club(1L, "Liverpool");
        Player player = Player.builder()
                .name("Steven Gerrard")
                .club(club)
                .build();
        PersonalDetails profile = PersonalDetails.builder()
                .player(player)
                .dob(Date.valueOf("1980-05-30"))
                .nationality("England")
                .height(185)
                .build();
        player.setProfile(profile);
        player.setId(1L);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());

        when(userService.getCurrentUser()).thenReturn(creator);
        when(clubRepository.findById(any(Long.class))).thenReturn(Optional.of(club));
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        PlayerResp playerResp = playerManagementService.create(playerReq);

        assertThat(playerResp).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerResp.getName()).isEqualTo(player.getName());
        assertThat(playerResp.getClub()).isEqualTo(player.getClub().getName());
    }

    @Test
    public void create_withNullClub_success() {
        User creator = User.builder()
                .username("adminTest")
                .build();
        Player player = Player.builder()
                .name("Steven Gerrard")
                .build();
        PersonalDetails profile = PersonalDetails.builder()
                .player(player)
                .dob(Date.valueOf("1980-05-30"))
                .nationality("England")
                .height(185)
                .build();
        player.setProfile(profile);
        player.setId(1L);
        PlayerReq playerReq = new PlayerReq(player.getName());

        when(userService.getCurrentUser()).thenReturn(creator);
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        PlayerResp playerResp = playerManagementService.create(playerReq);

        assertThat(playerResp).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerResp.getName()).isEqualTo(player.getName());
        assertThat(playerResp.getClub()).isEqualTo("Free Agent");
    }

    //============
    //---UPDATE---
    //============
    @Test
    public void update_success() {
        User editor = User.builder()
                .username("adminTest")
                .build();
        Club club = new Club(1L, "Liverpool");
        Player player = Player.builder()
                .name("Steven Gerrard")
                .club(club)
                .build();
        PersonalDetails profile = PersonalDetails.builder()
                .player(player)
                .dob(Date.valueOf("1980-05-30"))
                .nationality("England")
                .height(185)
                .build();
        player.setProfile(profile);
        player.setId(1L);
        String newPlayerName = player.getName() + " updated";
        PlayerReq playerReq = new PlayerReq(newPlayerName, club.getId());

        when(userService.getCurrentUser()).thenReturn(editor);
        when(clubRepository.findById(any(Long.class))).thenReturn(Optional.of(club));
        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        PlayerResp playerRes = playerManagementService.update(player.getId(), playerReq);

        assertThat(playerRes).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerRes.getName()).isEqualTo(newPlayerName);
    }

    @Test
    public void update_withNullClub_success() {
        User editor = User.builder()
                .username("adminTest")
                .build();
        Player player = Player.builder()
                .name("Steven Gerrard")
                .build();
        PersonalDetails profile = PersonalDetails.builder()
                .player(player)
                .dob(Date.valueOf("1980-05-30"))
                .nationality("England")
                .height(185)
                .build();
        player.setProfile(profile);
        player.setId(1L);
        PlayerReq playerReq = new PlayerReq(player.getName());

        when(userService.getCurrentUser()).thenReturn(editor);
        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        PlayerResp playerResp = playerManagementService.update(player.getId(), playerReq);

        assertThat(playerResp).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerResp.getName()).isEqualTo(player.getName());
        assertThat(playerResp.getClub()).isEqualTo("Free Agent");
    }

    @Test
    public void update_notFound() {
        Club club = new Club(1L, "Liverpool");
        Player player = Player.builder()
                .name("Steven Gerrard")
                .club(club)
                .build();
        PersonalDetails profile = PersonalDetails.builder()
                .player(player)
                .dob(Date.valueOf("1980-05-30"))
                .nationality("England")
                .height(185)
                .build();
        player.setProfile(profile);
        player.setId(1L);
        String newPlayerName = player.getName() + " updated";
        PlayerReq playerReq = new PlayerReq(newPlayerName, club.getId());

        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        PlayerResp playerResp = playerManagementService.update(player.getId(), playerReq);

        assertThat(playerResp).isNull();
    }

    //============
    //---DELETE---
    //============
    @Test
    public void delete_success() {
        Club club = new Club(1L, "Liverpool");
        Player player = Player.builder()
                .name("Steven Gerrard")
                .club(club)
                .build();
        PersonalDetails profile = PersonalDetails.builder()
                .player(player)
                .dob(Date.valueOf("1980-05-30"))
                .nationality("England")
                .height(185)
                .build();
        player.setProfile(profile);
        player.setId(1L);

        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.of(player));
        PlayerResp playerResp = playerManagementService.delete(player.getId());

        assertThat(playerResp).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerResp.getName()).isEqualTo(player.getName());
    }

    @Test
    public void delete_notFound() {
        Club club = new Club(1L, "Liverpool");
        Player player = Player.builder()
                .name("Steven Gerrard")
                .club(club)
                .build();
        PersonalDetails profile = PersonalDetails.builder()
                .player(player)
                .dob(Date.valueOf("1980-05-30"))
                .nationality("England")
                .height(185)
                .build();
        player.setProfile(profile);
        player.setId(1L);

        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        PlayerResp playerResp = playerManagementService.delete(player.getId());

        assertThat(playerResp).isNull();
    }
}
