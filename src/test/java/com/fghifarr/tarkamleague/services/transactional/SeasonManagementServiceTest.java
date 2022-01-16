package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.entities.SeasonClub;
import com.fghifarr.tarkamleague.models.requests.SeasonReq;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.SeasonClubRepository;
import com.fghifarr.tarkamleague.repositories.SeasonRepository;
import com.fghifarr.tarkamleague.services.SeasonClubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SeasonManagementServiceTest {
    @Mock
    SeasonRepository seasonRepository;
    @Mock
    ClubRepository clubRepository;
    @Mock
    SeasonClubRepository seasonClubRepository;
    @Mock
    SeasonClubService seasonClubService;

    SeasonManagementService seasonManagementService;
    @BeforeEach
    void injectDependencies() {
        seasonManagementService = new SeasonManagementService(
                seasonRepository, clubRepository, seasonClubRepository, seasonClubService);
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

    Season createSeason() {
        List<Club> clubList = createClubList();
        Season season = new Season(1L,"2021/22");

        for (Club club : clubList.subList(0, clubList.size() - 2)) {
            SeasonClub seasonClub = new SeasonClub();
            seasonClub.setSeason(season);
            seasonClub.setClub(club);
            club.getSeasons().add(seasonClub);
            season.getClubs().add(seasonClub);
        }

        return season;
    }

    //============
    //---CREATE---
    //============
    @Test
    public void create_success() {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(it -> it.getClub().getId()).collect(Collectors.toSet()));

        when(seasonRepository.save(any(Season.class))).thenReturn(season);
        SeasonResp seasonResp = seasonManagementService.create(seasonReq);

        assertThat(seasonResp).isExactlyInstanceOf(SeasonResp.class);
        assertThat(seasonResp.getName()).isEqualTo(season.getName());
    }

    //============
    //---UPDATE---
    //============
    @Test
    public void update_success() {
        List<Club> clubList = createClubList();
        Season season = createSeason();
        String newSeasonName = season.getName() + " updated";
        SeasonReq seasonReq = new SeasonReq(newSeasonName,
                season.getClubs().stream().map(it -> it.getClub().getId()).collect(Collectors.toSet()));

        when(seasonRepository.findById(any(Long.class))).thenReturn(Optional.of(season));
        when(clubRepository.findById(any(Long.class))).thenAnswer((Answer<Optional<Club>>) invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            Long id = (Long) args[0];

            return clubList.stream().filter(it -> Objects.equals(it.getId(), id)).findAny();
        });
        when(seasonRepository.save(any(Season.class))).thenReturn(season);
        SeasonResp seasonResp = seasonManagementService.update(season.getId(), seasonReq);

        assertThat(seasonResp).isExactlyInstanceOf(SeasonResp.class);
        assertThat(seasonResp.getName()).isEqualTo(newSeasonName);
    }

    @Test
    public void update_notFound() {
        Season season = createSeason();
        String newSeasonName = season.getName() + " updated";
        SeasonReq seasonReq = new SeasonReq(newSeasonName,
                season.getClubs().stream().map(it -> it.getClub().getId()).collect(Collectors.toSet()));

        when(seasonRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        SeasonResp seasonResp = seasonManagementService.update(season.getId(), seasonReq);

        assertThat(seasonResp).isNull();
    }

    //============
    //---DELETE---
    //============
    @Test
    public void delete_success() {
        Season season = createSeason();

        when(seasonRepository.findById(any(Long.class))).thenReturn(Optional.of(season));
        SeasonResp seasonResp = seasonManagementService.delete(season.getId());

        assertThat(seasonResp).isExactlyInstanceOf(SeasonResp.class);
        assertThat(seasonResp.getName()).isEqualTo(season.getName());
    }

    @Test
    public void delete_notFound() {
        Season season = createSeason();

        when(seasonRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        SeasonResp seasonResp = seasonManagementService.delete(season.getId());

        assertThat(seasonResp).isNull();
    }
}
