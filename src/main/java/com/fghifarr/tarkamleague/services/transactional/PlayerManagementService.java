package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.requests.PlayerReq;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PlayerManagementService {

    PlayerRepository playerRepository;
    ClubRepository clubRepository;

    PlayerManagementService(PlayerRepository playerRepository, ClubRepository clubRepository) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;
    }

    public PlayerResp create(PlayerReq playerReq) {
        Club club = clubRepository.findById(playerReq.getClub()).orElse(null);

        Player player = Player.builder()
                .name(playerReq.getName())
                .club(club)
                .build();
        save(player);

        return new PlayerResp(player);
    }

    public PlayerResp update(Long id, PlayerReq playerReq) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        if (playerOpt.isEmpty()) return null;
        Club club = clubRepository.findById(playerReq.getClub()).orElse(null);

        Player player = playerOpt.get();
        player.setName(playerReq.getName());
        player.setClub(club);
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
