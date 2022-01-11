package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.requests.ClubListingCriteria;
import com.fghifarr.tarkamleague.models.requests.PlayerListingCriteria;
import com.fghifarr.tarkamleague.models.responses.ClubResp;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import com.fghifarr.tarkamleague.services.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
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
        Club liverpool = new Club(1L, "Liverpool");
        Club manCity = new Club(2L, "Manchester City");
        Club manUtd = new Club(3L, "Manchester United");

        return List.of(
                new Player(7L, "Steven Gerrard"),
                new Player(8L, "Wayne Rooney"),
                new Player(9L, "Mohammed Salah", liverpool),
                new Player(10L, "Trent Alexander-Arnold", liverpool),
                new Player(11L, "Andrew Robertson", liverpool),
                new Player(12L, "David De Gea", manUtd),
                new Player(13L, "Kevin De Bruyne", manCity)
        );
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
}
