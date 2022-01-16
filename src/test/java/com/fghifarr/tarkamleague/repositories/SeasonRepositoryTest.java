package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.entities.SeasonClub;
import com.fghifarr.tarkamleague.models.requests.SeasonListingCriteria;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
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

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SeasonRepositoryTest {
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private SeasonRepository seasonRepository;
    @Autowired
    private SeasonClubRepository seasonClubRepository;

    //========================
    //-----DATA GENERATOR-----
    //========================
    List<Club> clubList = new ArrayList<>();
    List<Season> seasonList = new ArrayList<>();

    @BeforeEach
    void initData() {
        initClubData();
        initSeasonData();
    }

    void initClubData() {
        clubRepository.saveAllAndFlush(Set.of(
                new Club("Liverpool"),
                new Club("Manchester City"),
                new Club("Manchester United"),
                new Club("Newcastle United"),
                new Club("West Ham United"),
                new Club("Leicester City")
        ));
        clubList = clubRepository.findAll();
    }

    void initSeasonData() {
        List<String> seasonNameList = List.of(
                "2021/22", "2020/21", "2019/20", "2018/19", "2017/18"
        );

        for (String seasonName : seasonNameList) {
            Season season = new Season(seasonName);
            Collections.shuffle(clubList);
            List<SeasonClub> seasonClubList = new ArrayList<>();
            for (Club club : clubList.subList(0, clubList.size() - 2)) {
                SeasonClub seasonClub = new SeasonClub();
                seasonClub.setSeason(season);
                seasonClub.setClub(club);
                club.getSeasons().add(seasonClub);
                season.getClubs().add(seasonClub);
                seasonClubList.add(seasonClub);
            }
            seasonRepository.saveAndFlush(season);
            seasonClubRepository.saveAllAndFlush(seasonClubList);
        }
        seasonList = seasonRepository.findAll();
    }

    @AfterEach
    void deleteAll() {
        seasonClubRepository.deleteAll();
        seasonRepository.deleteAll();
        clubRepository.deleteAll();
        clubList = new ArrayList<>();
        seasonList = new ArrayList<>();
    }

    //==========================================
    //-----FIND ALL BY NAME WITH PAGINATION-----
    //==========================================
    @Test
    public void findAllByNameWithPagination_withoutFilter_success() {
        SeasonListingCriteria criteria = new SeasonListingCriteria();
        Page<SeasonResp> page = seasonRepository.findAllRespByNameLike(criteria.getQuery(), criteria);
        List<SeasonResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(seasonList.size());

        long idComparator = Integer.MIN_VALUE;
        for (SeasonResp seasonResp : result) {
            assertTrue(seasonResp.getId() > idComparator);
            idComparator = seasonResp.getId();
        }
    }

    @Test
    public void findAllByNameWithPagination_whenFilterOn_success() {
        String query = "21";
        String sortBy = "id";
        Sort.Direction order = Sort.Direction.DESC;
        int limit = 2;
        int offset = 0;

        List<String> ans = seasonList.stream()
                .map(Season::getName)
                .filter(name -> name.contains(query))
                .collect(Collectors.toList());
        SeasonListingCriteria criteria = new SeasonListingCriteria(
                query, offset, limit, sortBy, order);

        Page<SeasonResp> page = seasonRepository.findAllRespByNameLike(criteria.getQuery(), criteria);
        List<SeasonResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(Math.min(limit, ans.size()));

        long idComparator = Integer.MAX_VALUE;
        for (SeasonResp seasonResp : result) {
            assertThat(seasonResp.getName()).isIn(ans);
            assertTrue(seasonResp.getId() < idComparator);
            idComparator = seasonResp.getId();
        }
    }

    //====================
    //-----FIND BY ID-----
    //====================
    @Test
    public void findById_success() {
        Long id = 9L;
        SeasonResp seasonResp = seasonRepository.findRespById(id);

        assertThat(seasonResp.getId()).isEqualTo(id);
    }

    @Test
    public void findById_notFound() {
        Long id = 70L;
        SeasonResp seasonResp = seasonRepository.findRespById(id);

        assertThat(seasonResp).isNull();
    }
}
