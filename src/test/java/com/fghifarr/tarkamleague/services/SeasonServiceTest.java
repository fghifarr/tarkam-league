package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.entities.SeasonClub;
import com.fghifarr.tarkamleague.models.requests.SeasonListingCriteria;
import com.fghifarr.tarkamleague.models.responses.SeasonClubResp;
import com.fghifarr.tarkamleague.models.responses.SeasonDetailsResp;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
import com.fghifarr.tarkamleague.repositories.SeasonClubRepository;
import com.fghifarr.tarkamleague.repositories.SeasonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SeasonServiceTest {
    @Mock
    private SeasonRepository seasonRepository;
    @Mock
    private SeasonClubRepository seasonClubRepository;

    SeasonService seasonService;

    @BeforeEach
    void injectDependencies() {
        seasonService = new SeasonService(seasonRepository, seasonClubRepository);
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

    List<Season> createSeasonList() {
        List<Club> clubList = new ArrayList<>(createClubList());
        List<String> seasonNameList = List.of(
                "2021/22", "2020/21", "2019/20", "2018/19", "2017/18"
        );
        List<Season> seasonList = new ArrayList<>();

        for (String seasonName : seasonNameList) {
            Season season = new Season(seasonName);
            Collections.shuffle(clubList);
            for (Club club : clubList.subList(0, clubList.size() - 2)) {
                SeasonClub seasonClub = new SeasonClub();
                seasonClub.setSeason(season);
                seasonClub.setClub(club);
                club.getSeasons().add(seasonClub);
                season.getClubs().add(seasonClub);
                seasonList.add(season);
            }
        }

        return seasonList;
    }

    Page<SeasonResp> createSeasonPage() {
        List<Season> seasonList = createSeasonList();
        List<SeasonResp> seasonRespList = seasonList.stream()
                .map(SeasonResp::new)
                .collect(Collectors.toList());

        return new PageImpl<>(seasonRespList);
    }

    Season createSeason() {
        List<Club> clubList = createClubList();
        Season season = new Season(1L, "2021/22");

        for (Club club : clubList.subList(0, clubList.size() - 2)) {
            SeasonClub seasonClub = new SeasonClub();
            seasonClub.setSeason(season);
            seasonClub.setClub(club);
            club.getSeasons().add(seasonClub);
            season.getClubs().add(seasonClub);
        }

        return season;
    }

    //==============
    //-----LIST-----
    //==============
    @Test
    public void list_success() {
        Page<SeasonResp> seasonPage = createSeasonPage();

        when(seasonRepository.findAllRespByNameLike(any(String.class), any(Pageable.class))).thenReturn(seasonPage);

        Page<SeasonResp> page = seasonService.list(new SeasonListingCriteria());
        List<SeasonResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(seasonPage.getContent().size());
        assertThat(result.get(1)).isExactlyInstanceOf(SeasonResp.class);
        assertThat(result.get(1).getName()).isEqualTo(seasonPage.getContent().get(1).getName());
    }

    @Test
    public void list_notFound() {
        Page<SeasonResp> emptyPage = new PageImpl<>(new ArrayList<>());

        when(seasonRepository.findAllRespByNameLike(any(String.class), any(Pageable.class))).thenReturn(emptyPage);

        Page<SeasonResp> page = seasonService.list(new SeasonListingCriteria());
        List<SeasonResp> result = page.getContent();

        assertThat(result.size()).isEqualTo(0);
    }

    //=============
    //-----GET-----
    //=============
    @Test
    public void findById_success() {
        Season season = createSeason();
        List<SeasonClubResp> seasonClubRespList = season.getClubs().stream()
                .map(it -> new SeasonClubResp(
                        it.getSeason().getId(),
                        it.getSeason().getName(),
                        it.getClub().getName()))
                .collect(Collectors.toList());

        when(seasonClubRepository.findAllClubOfASeasonResp(any(Long.class))).thenReturn(seasonClubRespList);
        SeasonDetailsResp seasonDetailsResp = seasonService.get(season.getId());

        assertThat(seasonDetailsResp.getId()).isEqualTo(season.getId());
    }

    @Test
    public void findById_notFound() {
        when(seasonRepository.findRespById(any(Long.class))).thenReturn(null);
        SeasonResp seasonResp = seasonRepository.findRespById(1L);

        assertThat(seasonResp).isEqualTo(null);
    }
}
