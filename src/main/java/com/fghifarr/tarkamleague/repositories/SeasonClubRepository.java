package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.entities.SeasonClub;
import com.fghifarr.tarkamleague.entities.SeasonClubId;
import com.fghifarr.tarkamleague.models.responses.SeasonClubResp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeasonClubRepository extends JpaRepository<SeasonClub, SeasonClubId> {

    boolean existsSeasonClubBySeasonAndClub(Season season, Club club);
    List<SeasonClub> findAllBySeason(Season season);
    SeasonClub findBySeasonAndClub(Season season, Club club);

    @Query("SELECT " +
            "new com.fghifarr.tarkamleague.models.responses.SeasonClubResp(s.id, s.name, c.name) " +
            "FROM SeasonClub sc " +
            "LEFT JOIN sc.club c " +
            "LEFT JOIN sc.season s " +
            "WHERE s.id = ?1")
    List<SeasonClubResp> findAllClubOfASeasonResp(Long seasonId);
}
