package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.PersonalDetails;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.entities.User;
import com.fghifarr.tarkamleague.models.requests.PersonalDetailsReq;
import com.fghifarr.tarkamleague.models.requests.PlayerReq;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PersonalDetailsRepository;
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

import java.time.LocalDate;
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
    @Mock
    PersonalDetailsRepository personalDetailsRepository;

    PlayerManagementService playerManagementService;
    @BeforeEach
    void initDependencies() {
        playerManagementService = new PlayerManagementService(
                playerRepository,
                clubRepository,
                personalDetailsRepository,
                userService
        );
    }

    //========================
    //-----DATA GENERATOR-----
    //========================
    User createUser() {
        return User.builder()
                .id(1L)
                .username("adminTest")
                .build();
    }

    Club createClub() {
        Club club =  Club.builder()
                .name("Liverpool")
                .build();
        club.setId(1L);

        return club;
    }

    Player createPlayer(Club club) {
        Player player = Player.builder()
                .name("Steven Gerrard")
                .position(Player.Position.MIDFIELDER)
                .club(club)
                .build();
        player.setId(1L);
        PersonalDetails profile = PersonalDetails.builder()
                .player(player)
                .dob(LocalDate.parse("1980-05-30"))
                .nationality("England")
                .height(185)
                .build();
        profile.setPlayer(player);
        player.setProfile(profile);

        return player;
    }

    PlayerReq createPlayerRequest(Player player) {
        return PlayerReq.builder()
                .name(player.getName())
                .position(player.getPosition())
                .club(player.getClub() != null ? player.getClub().getId() : null)
                .profile(PersonalDetailsReq.builder()
                        .dob(player.getProfile().getDob())
                        .nationality(player.getProfile().getNationality())
                        .height(player.getProfile().getHeight())
                        .build())
                .build();
    }

    //============
    //---CREATE---
    //============
    @Test
    public void create_success() {
        User creator = createUser();
        Club club = createClub();
        Player player = createPlayer(club);
        PlayerReq playerReq = createPlayerRequest(player);

        when(userService.getCurrentUser()).thenReturn(creator);
        when(clubRepository.findById(any(Long.class))).thenReturn(Optional.of(club));
        when(personalDetailsRepository.save(any(PersonalDetails.class))).thenReturn(player.getProfile());
        PlayerResp playerResp = playerManagementService.create(playerReq);

        assertThat(playerResp).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerResp.getName()).isEqualTo(player.getName());
        assertThat(playerResp.getClub()).isEqualTo(player.getClub().getName());
    }

    @Test
    public void create_withNullClub_success() {
        User creator = createUser();
        Player player = createPlayer(null);
        PlayerReq playerReq = createPlayerRequest(player);

        when(userService.getCurrentUser()).thenReturn(creator);
        when(personalDetailsRepository.save(any(PersonalDetails.class))).thenReturn(player.getProfile());
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
        User editor = createUser();
        Club club = createClub();
        Player player = createPlayer(club);
        PlayerReq playerReq = createPlayerRequest(player);
        String newPlayerName = player.getName() + " updated";
        playerReq.setName(newPlayerName);

        when(userService.getCurrentUser()).thenReturn(editor);
        when(clubRepository.findById(any(Long.class))).thenReturn(Optional.of(club));
        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.of(player));
        when(personalDetailsRepository.save(any(PersonalDetails.class))).thenReturn(player.getProfile());
        PlayerResp playerResp = playerManagementService.update(player.getId(), playerReq);

        assertThat(playerResp).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerResp.getName()).isEqualTo(newPlayerName);
    }

    @Test
    public void update_withNullClub_success() {
        User editor = createUser();
        Player player = createPlayer(null);
        PlayerReq playerReq = createPlayerRequest(player);
        String newPlayerName = player.getName() + " updated";
        playerReq.setName(newPlayerName);

        when(userService.getCurrentUser()).thenReturn(editor);
        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.of(player));
        when(personalDetailsRepository.save(any(PersonalDetails.class))).thenReturn(player.getProfile());
        PlayerResp playerResp = playerManagementService.update(player.getId(), playerReq);

        assertThat(playerResp).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerResp.getName()).isEqualTo(player.getName());
        assertThat(playerResp.getClub()).isEqualTo("Free Agent");
    }

    @Test
    public void update_notFound() {
        Club club = createClub();
        Player player = createPlayer(club);
        PlayerReq playerReq = createPlayerRequest(player);
        String newPlayerName = player.getName() + " updated";
        playerReq.setName(newPlayerName);

        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        PlayerResp playerResp = playerManagementService.update(player.getId(), playerReq);

        assertThat(playerResp).isNull();
    }

    //============
    //---DELETE---
    //============
    @Test
    public void delete_success() {
        Club club = createClub();
        Player player = createPlayer(club);
        PlayerReq playerReq = createPlayerRequest(player);
        String newPlayerName = player.getName() + " updated";
        playerReq.setName(newPlayerName);

        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.of(player));
        PlayerResp playerResp = playerManagementService.delete(player.getId());

        assertThat(playerResp).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerResp.getName()).isEqualTo(player.getName());
    }

    @Test
    public void delete_notFound() {
        Club club = createClub();
        Player player = createPlayer(club);
        PlayerReq playerReq = createPlayerRequest(player);
        String newPlayerName = player.getName() + " updated";
        playerReq.setName(newPlayerName);

        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        PlayerResp playerResp = playerManagementService.delete(player.getId());

        assertThat(playerResp).isNull();
    }
}
