package com.fghifarr.tarkamleague.services;

import com.fghifarr.tarkamleague.models.requests.FixtureListingCriteria;
import com.fghifarr.tarkamleague.models.responses.FixtureResp;
import com.fghifarr.tarkamleague.repositories.MatchRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FixtureService {

    private final MatchRepository matchRepository;

    public FixtureService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Page<FixtureResp> list(FixtureListingCriteria criteria) {
        return matchRepository.findAllFixturesResp(criteria.getClubId(), criteria);
    }
}
