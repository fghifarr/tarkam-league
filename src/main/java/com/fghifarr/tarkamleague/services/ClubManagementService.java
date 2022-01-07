package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.models.requests.ClubReq;
import com.fghifarr.tarkamleague.models.responses.ClubResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ClubManagementService {

    private final ClubRepository clubRepository;

    public ClubManagementService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public ClubResp create(ClubReq clubReq) {
        Club club = Club.builder()
                .name(clubReq.getName())
                .build();
        save(club);

        return new ClubResp(club);
    }

    public ClubResp update(Long id, ClubReq clubReq) {
        Optional<Club> clubOpt = clubRepository.findById(id);
        if (clubOpt.isEmpty()) return null;

        Club club = clubOpt.get();
        club.setName(clubReq.getName());
        save(club);

        return new ClubResp(club);
    }

    public void save(Club club) {
        clubRepository.save(club);
    }

    public ClubResp delete(Long id) {
        Optional<Club> clubOpt = clubRepository.findById(id);
        if (clubOpt.isEmpty()) return null;

        Club club = clubOpt.get();
        clubRepository.delete(club);

        return new ClubResp(club);
    }
}
