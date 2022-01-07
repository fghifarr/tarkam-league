package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlayerManagementService {

    PlayerRepository playerRepository;

    PlayerManagementService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public String test() {
        return "Test";
    }
}
