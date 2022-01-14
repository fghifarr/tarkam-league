package com.fghifarr.tarkamleague.services.transactional;

import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.models.requests.SeasonReq;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
import com.fghifarr.tarkamleague.repositories.SeasonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SeasonManagementService {

    private final SeasonRepository seasonRepository;

    public SeasonManagementService(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    public SeasonResp create(SeasonReq seasonReq) {
        Season season = Season.builder()
                .name(seasonReq.getName())
                .build();
        save(season);

        return new SeasonResp(season);
    }

    public SeasonResp update(Long id, SeasonReq seasonReq) {
        Optional<Season> seasonOpt = seasonRepository.findById(id);
        if (seasonOpt.isEmpty()) return null;

        Season season = seasonOpt.get();
        season.setName(seasonReq.getName());
        save(season);

        return new SeasonResp(season);
    }

    public void save(Season season) {
        seasonRepository.save(season);
    }

    public SeasonResp delete(Long id) {
        Optional<Season> seasonOpt = seasonRepository.findById(id);
        if (seasonOpt.isEmpty()) return null;

        Season season = seasonOpt.get();
        seasonRepository.delete(season);

        return new SeasonResp(season);
    }
}
