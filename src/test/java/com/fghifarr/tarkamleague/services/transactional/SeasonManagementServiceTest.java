package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.entities.BaseEntity;
import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.models.requests.SeasonReq;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
import com.fghifarr.tarkamleague.repositories.SeasonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SeasonManagementServiceTest {
    @Mock
    SeasonRepository seasonRepository;

    SeasonManagementService seasonManagementService;
    @BeforeEach
    void injectDependencies() {
        seasonManagementService = new SeasonManagementService(seasonRepository);
    }

    //========================
    //-----DATA GENERATOR-----
    //========================
    Set<Club> createClubSet() {
        return Set.of(
                new Club(1L, "Liverpool"),
                new Club(2L, "Manchester City"),
                new Club(3L, "Manchester United"),
                new Club(4L, "Newcastle United"),
                new Club(5L, "West Ham United"),
                new Club(6L, "Leicester City")
        );
    }

    Season createSeason() {
        Set<Club> clubList = createClubSet();

        return new Season(1L, "2021/22", clubList);
    }

    //============
    //---CREATE---
    //============
    @Test
    public void create_success() {
        Season season = createSeason();
        SeasonReq seasonReq = new SeasonReq(season.getName(),
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));

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
        Season season = createSeason();
        String newSeasonName = season.getName() + " updated";
        SeasonReq seasonReq = new SeasonReq(newSeasonName,
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));

        when(seasonRepository.findById(any(Long.class))).thenReturn(Optional.of(season));
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
                season.getClubs().stream().map(BaseEntity::getId).collect(Collectors.toSet()));

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
