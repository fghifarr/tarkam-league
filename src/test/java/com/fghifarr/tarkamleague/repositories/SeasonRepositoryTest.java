package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Season;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    //========================
    //-----DATA GENERATOR-----
    //========================
    List<Club> clubList = new ArrayList<>();
    List<Season> seasonList = new ArrayList<>();

    @BeforeEach
    void initData() {
        clubRepository.saveAllAndFlush(Set.of(
                new Club(1L, "Liverpool"),
                new Club(2L, "Manchester City"),
                new Club(3L, "Manchester United"),
                new Club(4L, "Newcastle United"),
                new Club(5L, "West Ham United"),
                new Club(6L, "Leicester City")
        ));
        clubList = clubRepository.findAll();

        seasonRepository.saveAllAndFlush(List.of(
                new Season("2021/22", new HashSet<>(clubList)),
                new Season("2020/21", new HashSet<>(clubList)),
                new Season("2019/20", new HashSet<>(clubList)),
                new Season("2018/19", new HashSet<>(clubList)),
                new Season("2017/18", new HashSet<>(clubList))
        ));
        seasonList = seasonRepository.findAll();
    }

    @AfterEach
    void deleteAll() {
        seasonRepository.deleteAll();
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
