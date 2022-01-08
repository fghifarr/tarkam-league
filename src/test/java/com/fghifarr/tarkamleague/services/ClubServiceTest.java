package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.models.requests.ClubListingCriteria;
import com.fghifarr.tarkamleague.models.responses.ClubResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
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
public class ClubServiceTest {
    @Mock
    private ClubRepository clubRepository;

    ClubService clubService;

    @BeforeEach
    void injectDependencies() {
        clubService = new ClubService(clubRepository);
    }

    //========================
    //-----DATA GENERATOR-----
    //========================
    List<Club> createClubList() {
        return List.of(
                new Club(1L, "Liverpool"),
                new Club(2L, "Manchester City"),
                new Club(3L, "Manchester United"),
                new Club(4L, "Newcastle United"),
                new Club(5L, "West Ham United"),
                new Club(6L, "Leicester City")
        );
    }

    Page<ClubResp> createClubPage() {
        List<Club> clubList = createClubList();
        List<ClubResp> clubRespList = clubList.stream()
                .map(ClubResp::new)
                .collect(Collectors.toList());

        return new PageImpl<>(clubRespList);
    }

    //==============
    //-----LIST-----
    //==============
    @Test
    public void list_success() {
        Page<ClubResp> clubPage = createClubPage();

        when(clubRepository.findAllRespByNameLike(any(String.class), any(Pageable.class))).thenReturn(clubPage);

        Page<ClubResp> page = clubService.list(new ClubListingCriteria());
        List<ClubResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(clubPage.getContent().size());
        assertThat(result.get(1)).isExactlyInstanceOf(ClubResp.class);
        assertThat(result.get(1).getName()).isEqualTo(clubPage.getContent().get(1).getName());
    }

    @Test
    public void list_notFound() {
        Page<ClubResp> emptyPage = new PageImpl<>(new ArrayList<>());

        when(clubRepository.findAllRespByNameLike(any(String.class), any(Pageable.class))).thenReturn(emptyPage);

        Page<ClubResp> page = clubService.list(new ClubListingCriteria());
        List<ClubResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(0);
    }

    //=============
    //-----GET-----
    //=============
    @Test
    public void findById_success() {
        Club liverpool = new Club(1L, "Liverpool");

        when(clubRepository.findRespById(any(Long.class))).thenReturn(new ClubResp(liverpool));
        ClubResp club = clubRepository.findRespById(liverpool.getId());

        assertThat(club.getId()).isEqualTo(liverpool.getId());
    }

    @Test
    public void findById_notFound() {
        when(clubRepository.findRespById(any(Long.class))).thenReturn(null);
        ClubResp club = clubRepository.findRespById(1L);

        assertThat(club).isEqualTo(null);
    }
}
