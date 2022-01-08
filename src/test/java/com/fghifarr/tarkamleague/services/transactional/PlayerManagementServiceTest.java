package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.requests.PlayerReq;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerManagementServiceTest {

    @Mock
    PlayerRepository playerRepository;
    @Mock
    ClubRepository clubRepository;

    PlayerManagementService playerManagementService;
    @BeforeEach
    void initDependencies() {
        playerManagementService = new PlayerManagementService(playerRepository, clubRepository);
    }

    //============
    //---CREATE---
    //============
    @Test
    public void create_success() {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        PlayerReq playerReq = new PlayerReq(player.getName(), club.getId());

        when(clubRepository.findById(any(Long.class))).thenReturn(Optional.of(club));
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        PlayerResp playerResp = playerManagementService.create(playerReq);

        assertThat(playerResp).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerResp.getName()).isEqualTo(player.getName());
        assertThat(playerResp.getClub()).isEqualTo(player.getClub().getName());
    }

    //============
    //---UPDATE---
    //============
    @Test
    public void update_success() {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
        String newPlayerName = player.getName() + " updated";
        PlayerReq playerReq = new PlayerReq(newPlayerName, club.getId());

        when(clubRepository.findById(any(Long.class))).thenReturn(Optional.of(club));
        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        PlayerResp playerRes = playerManagementService.update(player.getId(), playerReq);

        assertThat(playerRes).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerRes.getName()).isEqualTo(newPlayerName);
    }

    @Test
    public void update_notFound() {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);
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
        Player player = new Player(2L, "Steven Gerrard", club);

        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.of(player));
        PlayerResp playerResp = playerManagementService.delete(player.getId());

        assertThat(playerResp).isExactlyInstanceOf(PlayerResp.class);
        assertThat(playerResp.getName()).isEqualTo(player.getName());
    }

    @Test
    public void delete_notFound() {
        Club club = new Club(1L, "Liverpool");
        Player player = new Player(2L, "Steven Gerrard", club);

        when(playerRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        PlayerResp playerResp = playerManagementService.delete(player.getId());

        assertThat(playerResp).isNull();
    }
}
