package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.entities.User;
import com.fghifarr.tarkamleague.models.requests.PlayerReq;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import com.fghifarr.tarkamleague.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class PlayerManagementService {

    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;

    private final UserService userService;

    PlayerManagementService(PlayerRepository playerRepository,
                            ClubRepository clubRepository, UserService userService) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;

        this.userService = userService;
    }

    public PlayerResp create(PlayerReq playerReq) {
        Club club = null;
        if (playerReq.getClub() != null)
            club = clubRepository.findById(playerReq.getClub()).orElse(null);

        User creator = userService.getCurrentUser();

        Player player = Player.builder()
                .name(playerReq.getName())
                .club(club)
                .build();
        player.setCreatedBy(creator);
        player.setModifiedBy(creator);
        save(player);

        return new PlayerResp(player);
    }

    public PlayerResp update(Long id, PlayerReq playerReq) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        if (playerOpt.isEmpty()) return null;
        Club club = null;
        if (playerReq.getClub() != null)
            club = clubRepository.findById(playerReq.getClub()).orElse(null);

        User editor = userService.getCurrentUser();

        Player player = playerOpt.get();
        player.setName(playerReq.getName());
        player.setClub(club);
        player.setModifiedBy(editor);
        player.setLastUpdated(new Date());
        save(player);

        return new PlayerResp(player);
    }

    public void save(Player player) {
        playerRepository.save(player);
    }

    public PlayerResp delete(Long id) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        if (playerOpt.isEmpty()) return null;

        Player player = playerOpt.get();
        playerRepository.delete(player);

        return new PlayerResp(player);
    }
}
