package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.PersonalDetails;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.requests.PlayerListingCriteria;
import com.fghifarr.tarkamleague.models.requests.init.InitPlayerReq;
import com.fghifarr.tarkamleague.models.responses.PlayerDetailsResp;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    PlayerRepository playerRepository;

    PlayerService playerService;
    @BeforeEach
    void initDependencies() {
        playerService = new PlayerService(playerRepository);
    }

    //========================
    //-----DATA GENERATOR-----
    //========================
    List<Player> createPlayerList() {
        //Club list
        List<Club> clubList = List.of(
                new Club(1L, "Liverpool"),
                new Club(2L, "Manchester City"),
                new Club(3L, "Manchester United")
        );
        //Raw player list
        List<InitPlayerReq> playerReqs = List.of(
                new InitPlayerReq("Steven Gerrard", Player.Position.MIDFIELDER, "", LocalDate.parse("1980-05-30"), "England", 185),
                new InitPlayerReq("Wayne Rooney", Player.Position.FORWARD, "", LocalDate.parse("1985-10-24"), "England", 176),
                new InitPlayerReq("Mohamed Salah", Player.Position.FORWARD, "Liverpool", LocalDate.parse("1992-06-15"), "Egypt", 175),
                new InitPlayerReq("Trent Alexander-Arnold", Player.Position.DEFENDER, "Liverpool", LocalDate.parse("1998-10-07"), "England", 175),
                new InitPlayerReq("Andrew Robertson", Player.Position.DEFENDER, "Liverpool", LocalDate.parse("1994-03-11"), "Scotland", 178),
                new InitPlayerReq("David de Gea", Player.Position.GOALKEEPER, "Manchester United", LocalDate.parse("1990-11-07"), "Spain", 192),
                new InitPlayerReq("Kevin De Bruyne", Player.Position.MIDFIELDER, "Manchester City", LocalDate.parse("1991-06-28"), "Belgium", 181)
        );
        //Player List
        List<Player> playerList = new ArrayList<>();
        long counter = 1L;
        for (InitPlayerReq playerReq : playerReqs) {
            Club club = clubList.stream()
                    .filter(it -> Objects.equals(it.getName(), playerReq.getName()))
                    .findAny().orElse(null);
            Player player = Player.builder()
                    .name(playerReq.getName())
                    .club(club)
                    .build();
            player.setId(counter);
            PersonalDetails profile = PersonalDetails.builder()
                    .dob(playerReq.getDob())
                    .nationality(playerReq.getNationality())
                    .height(playerReq.getHeight())
                    .player(player)
                    .build();
            player.setProfile(profile);

            playerList.add(player);
            counter++;
        }

        return playerList;
    }

    Page<PlayerResp> createPlayerPage() {
        List<Player> playerList = createPlayerList();
        List<PlayerResp> playerRespList = playerList.stream()
                .map(PlayerResp::new)
                .collect(Collectors.toList());

        return new PageImpl<>(playerRespList);
    }

    //==============
    //-----LIST-----
    //==============
    @Test
    public void list_success() {
        Page<PlayerResp> playerPage = createPlayerPage();

        when(playerRepository.findAllRespWithQueries(any(String.class), any(), any(Pageable.class)))
                .thenReturn(playerPage);

        Page<PlayerResp> page = playerService.list(new PlayerListingCriteria());
        List<PlayerResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(playerPage.getContent().size());
        assertThat(result.get(1)).isExactlyInstanceOf(PlayerResp.class);
        assertThat(result.get(1).getName()).isEqualTo(playerPage.getContent().get(1).getName());
    }

    @Test
    public void list_notFound() {
        Page<PlayerResp> emptyPage = new PageImpl<>(new ArrayList<>());

        when(playerRepository.findAllRespWithQueries(any(String.class), any(), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<PlayerResp> page = playerService.list(new PlayerListingCriteria());
        List<PlayerResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(0);
    }

    //=============
    //-----GET-----
    //=============
    @Test
    public void findRespById_success() {
        Player player = Player.builder()
                .name("Steven Gerrard")
                .build();
        PersonalDetails profile = PersonalDetails.builder()
                .player(player)
                .dob(LocalDate.parse("1980-05-30"))
                .nationality("England")
                .height(185)
                .build();
        player.setProfile(profile);
        player.setId(1L);

        when(playerRepository.findRespById(any(Long.class))).thenReturn(new PlayerDetailsResp(player));
        PlayerDetailsResp playerResp = playerRepository.findRespById(player.getId());

        assertThat(playerResp.getId()).isEqualTo(player.getId());
    }

    @Test
    public void findRespById_notFound() {
        when(playerRepository.findRespById(any(Long.class))).thenReturn(null);
        PlayerDetailsResp playerResp = playerRepository.findRespById(1L);

        assertThat(playerResp).isEqualTo(null);
    }
}
