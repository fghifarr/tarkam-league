package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.requests.PlayerListingCriteria;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlayerRepositoryTest {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ClubRepository clubRepository;

    //========================
    //-----DATA GENERATOR-----
    //========================
    Club liverpool = new Club(1L, "Liverpool");
    Club manCity = new Club(2L, "Manchester City");
    Club manUtd = new Club(3L, "Manchester United");

    List<Club> clubList = List.of(
            liverpool, manCity, manUtd,
            new Club(4L, "Newcastle United"),
            new Club(5L, "West Ham United"),
            new Club(6L, "Leicester City")
    );

    List<Player> playerList = List.of(
            new Player(7L, "Steven Gerrard"),
            new Player(8L, "Wayne Rooney"),
            new Player(9L, "Mohammed Salah", liverpool),
            new Player(10L, "Trent Alexander-Arnold", liverpool),
            new Player(11L, "Andrew Robertson", liverpool),
            new Player(12L, "David De Gea", manUtd),
            new Player(13L, "Kevin De Bruyne", manCity)
    );

    @BeforeEach
    public void initData() {
        clubRepository.saveAllAndFlush(clubList);
        playerRepository.saveAllAndFlush(playerList);
    }

    @AfterEach
    public void deleteAll() {
        playerRepository.deleteAll();
        clubRepository.deleteAll();
    }

    //==================================
    //----FIND ALL RESP WITH QUERIES----
    //==================================
    @Test
    public void findAllRespWithQueries_defaultQueries_success() {
        PlayerListingCriteria criteria = new PlayerListingCriteria();
        Page<PlayerResp> page = playerRepository
                .findAllRespWithQueries(criteria.getQuery(), criteria.getClub(), criteria);
        List<PlayerResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(playerList.size());

        long idComparator = Integer.MIN_VALUE;
        for (PlayerResp player : result) {
            assertTrue(player.getId() > idComparator);
            idComparator = player.getId();
        }
    }

    @Test
    public void findAllRespWithQueries_success() {
        String query = "aNd";
        Long clubId = liverpool.getId();
        String sortBy = "id";
        Sort.Direction order = Sort.Direction.DESC;
        int limit = 12;
        int offset = 0;
        PlayerListingCriteria criteria = new PlayerListingCriteria(
                query, clubId, offset, limit, sortBy, order
        );
        List<String> ans = playerList.stream()
                .filter(player -> player.getName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                .filter(player -> player.getClub() != null && Objects.equals(player.getClub().getId(), clubId))
                .map(Player::getName)
                .collect(Collectors.toList());

        Page<PlayerResp> page = playerRepository
                .findAllRespWithQueries(criteria.getQuery(), criteria.getClub(), criteria);
        List<PlayerResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(Math.min(ans.size(), limit));

        long idComparator = Integer.MAX_VALUE;
        for (PlayerResp player : result) {
            assertThat(player.getName()).isIn(ans);
            assertTrue(player.getId() < idComparator);
            idComparator = player.getId();
        }
    }

    //=======================
    //----FIND RESP BY ID----
    //=======================
    @Test
    public void findRespById_success() {
        Long id = 7L;
        PlayerResp player = playerRepository.findRespById(id);

        assertThat(player.getId()).isEqualTo(id);
    }

    @Test
    public void findRespById_notFound() {
        Long id = 70L;
        PlayerResp playerResp = playerRepository.findRespById(id);

        assertThat(playerResp).isNull();
    }
}
