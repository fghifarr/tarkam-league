package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.models.requests.SeasonListingCriteria;
import com.fghifarr.tarkamleague.models.responses.SeasonClubResp;
import com.fghifarr.tarkamleague.models.responses.SeasonDetailsResp;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
import com.fghifarr.tarkamleague.repositories.SeasonClubRepository;
import com.fghifarr.tarkamleague.repositories.SeasonRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final SeasonClubRepository seasonClubRepository;

    public SeasonService(SeasonRepository seasonRepository,
                         SeasonClubRepository seasonClubRepository) {
        this.seasonRepository = seasonRepository;
        this.seasonClubRepository = seasonClubRepository;
    }

    public Page<SeasonResp> list(SeasonListingCriteria criteria) {
        return seasonRepository.findAllRespByNameLike(criteria.getQuery(), criteria);
    }

    public SeasonDetailsResp get(Long id) {
        List<SeasonClubResp> seasonClubRespList = seasonClubRepository.findAllClubOfASeasonResp(id);
        if (seasonClubRespList.isEmpty())
            return null;

        return new SeasonDetailsResp(seasonClubRespList);
    }
}
