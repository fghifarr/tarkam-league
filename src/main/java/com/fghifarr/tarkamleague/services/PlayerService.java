package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.models.requests.PlayerListingCriteria;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import com.fghifarr.tarkamleague.repositories.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PlayerService {

    PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Page<PlayerResp> list(PlayerListingCriteria criteria) {
        return playerRepository.findAllRespWithQueries(criteria.getQuery(), criteria.getClub(), criteria);
    }

    public PlayerResp get(Long id) {
        return playerRepository.findRespById(id);
    }
}
