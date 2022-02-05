package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.configs.planner.MatchPlanner;
import com.fghifarr.tarkamleague.entities.Match;
import com.fghifarr.tarkamleague.models.requests.ListingCriteria;
import com.fghifarr.tarkamleague.models.responses.FixtureResp;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT " +
            "new com.fghifarr.tarkamleague.models.responses.FixtureResp(m.kickOff, mhc.name, mvc.name) " +
            "FROM Match m " +
            "LEFT JOIN m.host mh " +
            "LEFT JOIN mh.club mhc " +
            "LEFT JOIN m.visitor mv " +
            "LEFT JOIN mv.club mvc " +
            "WHERE m.isFinished = false")
    Page<FixtureResp> findAllFixturesResp(Long clubId, ListingCriteria listingCriteria);

    @Query("SELECT " +
            "new com.fghifarr.tarkamleague.configs.planner.MatchPlanner(" +
            "m.id, m.kickOff, mh.id, mhc.id, mhc.name, mv.id, mvc.id, mvc.name) " +
            "FROM Match m " +
            "LEFT JOIN m.host mh " +
            "LEFT JOIN mh.club mhc " +
            "LEFT JOIN m.visitor mv " +
            "LEFT JOIN mv.club mvc " +
            "WHERE m.isFinished = false")
    List<MatchPlanner> findAllMatchPlanners();
}
