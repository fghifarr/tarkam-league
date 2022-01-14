package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.models.requests.SeasonListingCriteria;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
import com.fghifarr.tarkamleague.repositories.SeasonRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SeasonService {

    private final SeasonRepository seasonRepository;

    public SeasonService(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    public Page<SeasonResp> list(SeasonListingCriteria criteria) {
        return seasonRepository.findAllRespByNameLike(criteria.getQuery(), criteria);
    }

    public SeasonResp get(Long id) {
        return seasonRepository.findRespById(id);
    }
}
