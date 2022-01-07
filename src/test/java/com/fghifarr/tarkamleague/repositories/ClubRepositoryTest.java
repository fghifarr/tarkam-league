package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.models.requests.ClubListingCriteria;
import com.fghifarr.tarkamleague.models.responses.ClubResp;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClubRepositoryTest {
    @Autowired
    private ClubRepository clubRepository;

    //========================
    //-----DATA GENERATOR-----
    //========================
    Club liverpool = new Club(1L, "Liverpool");
    Club manCity = new Club(2L, "Manchester City");
    Club manUtd = new Club(3L, "Manchester United");
    Club newcastle = new Club(4L, "Newcastle United");
    Club westHam = new Club(5L, "West Ham United");
    Club leicester = new Club(6L, "Leicester City");

    List<Club> clubList = List.of(
            liverpool, manCity, manUtd, newcastle, westHam, leicester
    );

    @BeforeEach
    void initClub() {
        clubRepository.saveAllAndFlush(clubList);
    }

    @AfterEach
    void deleteAll() {
        clubRepository.deleteAll();
    }

    //==========================
    //-----FIND ALL BY NAME-----
    //==========================
    @Test
    public void findAllByName_success() {
        String query = "ster";
        List<String> ans = clubList.stream()
                .map(Club::getName)
                .filter(name -> name.contains(query))
                .collect(Collectors.toList());

        List<Club> result = clubRepository.findAllRespByNameLike(query);

        assertThat(result.size()).isEqualTo(ans.size());
        for (Club club : result) {
            assertThat(club.getName()).isIn(ans);
        }
    }

    @Test
    public void findAllByName_null() {
        String query = "qwerty";

        List<Club> result = clubRepository.findAllRespByNameLike(query);

        assertThat(result.size()).isEqualTo(0);
    }

    //==========================================
    //-----FIND ALL BY NAME WITH PAGINATION-----
    //==========================================
    @Test
    public void findAllByNameWithPagination_withoutFilter_success() {
        ClubListingCriteria criteria = new ClubListingCriteria();
        Page<ClubResp> page = clubRepository.findAllRespByNameLike(criteria.getQuery(), criteria);
        List<ClubResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(clubList.size());

        long idComparator = Integer.MIN_VALUE;
        for (ClubResp club : result) {
            assertTrue(club.getId() > idComparator);
            idComparator = club.getId();
        }
    }

    @Test
    public void findAllByNameWithPagination_whenFilterOn_success() {
        String query = "ster";
        String sortBy = "id";
        Sort.Direction order = Sort.Direction.DESC;
        int limit = 2;
        int offset = 1;

        List<String> ans = clubList.stream()
                .map(Club::getName)
                .filter(name -> name.contains(query))
                .collect(Collectors.toList());
        ClubListingCriteria criteria = new ClubListingCriteria(
                query, offset, limit, sortBy, order);

        Page<ClubResp> page = clubRepository.findAllRespByNameLike(criteria.getQuery(), criteria);
        List<ClubResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(limit);

        long idComparator = Integer.MAX_VALUE;
        for (ClubResp club : result) {
            assertThat(club.getName()).isIn(ans);
            assertTrue(club.getId() < idComparator);
            idComparator = club.getId();
        }
    }

    //====================
    //-----FIND BY ID-----
    //====================
    @Test
    public void findById_success() {
        Long id = 1L;
        ClubResp club = clubRepository.findRespById(id);

        assertThat(club.getId()).isEqualTo(id);
    }

    @Test
    public void findById_notFound() {
        Long id = 70L;
        ClubResp club = clubRepository.findRespById(id);

        assertThat(club).isNull();
    }
}
