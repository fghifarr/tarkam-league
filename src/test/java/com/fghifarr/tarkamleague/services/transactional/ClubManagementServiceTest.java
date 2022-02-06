package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.models.requests.ClubReq;
import com.fghifarr.tarkamleague.models.responses.ClubResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import com.fghifarr.tarkamleague.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClubManagementServiceTest {
    @Mock
    ClubRepository clubRepository;
    @Mock
    PlayerRepository playerRepository;
    @Mock
    UserService userService;

    ClubManagementService clubManagementService;
    @BeforeEach
    void injectDependencies() {
        clubManagementService = new ClubManagementService(clubRepository, playerRepository, userService);
    }

    //============
    //---CREATE---
    //============
    @Test
    public void create_success() {
        Club club = new Club(1L, "Liverpool");
        ClubReq clubReq = new ClubReq(club.getName());

        when(clubRepository.save(any(Club.class))).thenReturn(club);
        ClubResp clubResp = clubManagementService.create(clubReq);

        assertThat(clubResp).isExactlyInstanceOf(ClubResp.class);
        assertThat(clubResp.getName()).isEqualTo(club.getName());
    }

    //============
    //---UPDATE---
    //============
    @Test
    public void update_success() {
        Club club = new Club(1L, "Liverpool");
        String newClubName = club.getName() + " updated";
        ClubReq clubReq = new ClubReq(newClubName);

        when(clubRepository.findById(any(Long.class))).thenReturn(Optional.of(club));
        when(clubRepository.save(any(Club.class))).thenReturn(club);
        ClubResp clubResp = clubManagementService.update(club.getId(), clubReq);

        assertThat(clubResp).isExactlyInstanceOf(ClubResp.class);
        assertThat(clubResp.getName()).isEqualTo(newClubName);
    }

    @Test
    public void update_notFound() {
        Club club = new Club(1L, "Liverpool");
        String newClubName = club.getName() + " updated";
        ClubReq clubReq = new ClubReq(newClubName);

        when(clubRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        ClubResp clubResp = clubManagementService.update(club.getId(), clubReq);

        assertThat(clubResp).isNull();
    }

    //============
    //---DELETE---
    //============
    @Test
    public void delete_success() {
        Club club = new Club(1L, "Liverpool");

        when(clubRepository.findById(any(Long.class))).thenReturn(Optional.of(club));
        ClubResp clubResp = clubManagementService.delete(club.getId());

        assertThat(clubResp).isExactlyInstanceOf(ClubResp.class);
        assertThat(clubResp.getName()).isEqualTo(club.getName());
    }

    @Test
    public void delete_notFound() {
        Club club = new Club(1L, "Liverpool");

        when(clubRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        ClubResp clubResp = clubManagementService.delete(club.getId());

        assertThat(clubResp).isNull();
    }
}
