package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.requests.ClubReq;
import com.fghifarr.tarkamleague.models.responses.ClubResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import com.fghifarr.tarkamleague.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClubManagementService {

    private final ClubRepository clubRepository;
    private final PlayerRepository playerRepository;
    private final UserService userService;

    public ClubManagementService(ClubRepository clubRepository,
                                 PlayerRepository playerRepository,
                                 UserService userService) {
        this.clubRepository = clubRepository;
        this.playerRepository = playerRepository;
        this.userService = userService;
    }

    public ClubResp create(ClubReq clubReq) {
        Club club = Club.builder()
                .name(clubReq.getName())
                .build();
        club.setCreatedBy(userService.getCurrentUser());
        club.setModifiedBy(userService.getCurrentUser());
        clubRepository.save(club);

        return new ClubResp(club);
    }

    public ClubResp update(Long id, ClubReq clubReq) {
        Optional<Club> clubOpt = clubRepository.findById(id);
        if (clubOpt.isEmpty()) return null;

        Club club = clubOpt.get();
        club.setName(clubReq.getName());
        club.setModifiedBy(userService.getCurrentUser());
        club.setLastUpdated(LocalDateTime.now());
        clubRepository.save(club);

        return new ClubResp(club);
    }

    public ClubResp delete(Long id) {
        Optional<Club> clubOpt = clubRepository.findById(id);
        if (clubOpt.isEmpty()) return null;

        Club club = clubOpt.get();
        List<Player> playerList = playerRepository.findAllByClub(club);
        for (Player player : playerList) {
            player.setClub(null);
            player.setModifiedBy(userService.getCurrentUser());
            player.setLastUpdated(LocalDateTime.now());
        }
        clubRepository.delete(club);

        return new ClubResp(club);
    }
}
