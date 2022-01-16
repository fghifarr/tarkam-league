package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.PersonalDetails;
import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.entities.User;
import com.fghifarr.tarkamleague.models.requests.PersonalDetailsReq;
import com.fghifarr.tarkamleague.models.requests.PlayerReq;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import com.fghifarr.tarkamleague.repositories.PersonalDetailsRepository;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import com.fghifarr.tarkamleague.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class PlayerManagementService {

    private final PlayerRepository playerRepository;
    private final PersonalDetailsRepository personalDetailsRepository;
    private final ClubRepository clubRepository;

    private final UserService userService;

    PlayerManagementService(PlayerRepository playerRepository,
                            ClubRepository clubRepository,
                            PersonalDetailsRepository personalDetailsRepository,
                            UserService userService) {
        this.playerRepository = playerRepository;
        this.personalDetailsRepository = personalDetailsRepository;
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
                .position(playerReq.getPosition())
                .club(club)
                .build();
        player.setCreatedBy(creator);
        player.setModifiedBy(creator);
        PersonalDetails profile = PersonalDetails.builder()
                .player(player)
                .dob(playerReq.getProfile().getDob())
                .nationality(playerReq.getProfile().getNationality())
                .height(playerReq.getProfile().getHeight())
                .build();
        player.setProfile(profile);
        saveByPersonalDetails(player);

        return new PlayerResp(player);
    }

    public PlayerResp update(Long id, PlayerReq playerReq) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        if (playerOpt.isEmpty()) return null;
        Club club = null;
        if (playerReq.getClub() != null)
            club = clubRepository.findById(playerReq.getClub()).orElse(null);

        User editor = userService.getCurrentUser();
        PersonalDetailsReq profileReq = playerReq.getProfile();

        Player player = playerOpt.get();
        player.setName(playerReq.getName());
        player.setPosition(playerReq.getPosition());
        player.setClub(club);
        player.setModifiedBy(editor);
        player.setLastUpdated(LocalDateTime.now());
        player.getProfile().setDob(profileReq.getDob());
        player.getProfile().setNationality(profileReq.getNationality());
        player.getProfile().setHeight(profileReq.getHeight());
        saveByPersonalDetails(player);

        return new PlayerResp(player);
    }

    public void saveByPersonalDetails(Player player) {
        personalDetailsRepository.save(player.getProfile());
    }

    public PlayerResp delete(Long id) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        if (playerOpt.isEmpty()) return null;

        Player player = playerOpt.get();
        playerRepository.delete(player);

        return new PlayerResp(player);
    }
}
