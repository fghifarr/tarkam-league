package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.models.requests.ClubListingCriteria;
import com.fghifarr.tarkamleague.models.responses.ClubResp;
import com.fghifarr.tarkamleague.repositories.ClubRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;

    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public Page<ClubResp> list(ClubListingCriteria criteria) {
        return clubRepository.findAllRespByNameLike(criteria.getQuery(), criteria);
    }

    public ClubResp get(Long id) {
        return clubRepository.findRespById(id);
    }
}
