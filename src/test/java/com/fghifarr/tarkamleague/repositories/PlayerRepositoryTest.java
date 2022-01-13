package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.PersonalDetails;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.requests.PlayerListingCriteria;
import com.fghifarr.tarkamleague.models.requests.init.InitPlayerReq;
import com.fghifarr.tarkamleague.models.responses.PlayerDetailsResp;
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

import java.sql.Date;
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
    private PersonalDetailsRepository personalDetailsRepository;
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

    List<InitPlayerReq> playerReqs = List.of(
            new InitPlayerReq("Steven Gerrard", Player.Position.MIDFIELDER, "", Date.valueOf("1980-05-30"), "England", 185),
            new InitPlayerReq("Wayne Rooney", Player.Position.FORWARD, "", Date.valueOf("1985-10-24"), "England", 176),
            new InitPlayerReq("Mohamed Salah", Player.Position.FORWARD, "Liverpool", Date.valueOf("1992-06-15"), "Egypt", 175),
            new InitPlayerReq("Trent Alexander-Arnold", Player.Position.DEFENDER, "Liverpool", Date.valueOf("1998-10-07"), "England", 175),
            new InitPlayerReq("Andrew Robertson", Player.Position.DEFENDER, "Liverpool", Date.valueOf("1994-03-11"), "Scotland", 178),
            new InitPlayerReq("David de Gea", Player.Position.GOALKEEPER, "Manchester United", Date.valueOf("1990-11-07"), "Spain", 192),
            new InitPlayerReq("Kevin De Bruyne", Player.Position.MIDFIELDER, "Manchester City", Date.valueOf("1991-06-28"), "Belgium", 181)
    );

    @BeforeEach
    public void initData() {
        clubRepository.saveAllAndFlush(clubList);

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
            personalDetailsRepository.saveAndFlush(profile);
        }
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

        assertThat(result.size()).isEqualTo(playerReqs.size());

        long idComparator = Integer.MIN_VALUE;
        for (PlayerResp player : result) {
            assertTrue(player.getId() > idComparator);
            idComparator = player.getId();
        }
    }

    @Test
    public void findAllRespWithQueries_success() {
        String query = "aNd";
        Club club = liverpool;
        String sortBy = "id";
        Sort.Direction order = Sort.Direction.DESC;
        int limit = 12;
        int offset = 0;
        PlayerListingCriteria criteria = new PlayerListingCriteria(
                query, club.getId(), offset, limit, sortBy, order
        );
        List<String> ans = playerReqs.stream()
                .filter(player -> player.getName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                .filter(player -> player.getClub() != null && Objects.equals(player.getClub(), club.getName()))
                .map(InitPlayerReq::getName)
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
        PlayerDetailsResp playerDetails = playerRepository.findRespById(id);

        assertThat(playerDetails.getId()).isEqualTo(id);
    }

    @Test
    public void findRespById_notFound() {
        Long id = 70L;
        PlayerDetailsResp playerDetails = playerRepository.findRespById(id);

        assertThat(playerDetails).isNull();
    }
}
