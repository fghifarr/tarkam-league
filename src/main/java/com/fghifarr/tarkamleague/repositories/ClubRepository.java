package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.configs.planner.ClubPlanner;
import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.models.responses.ClubResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long> {

    @Query(value = "SELECT " +
            "new com.fghifarr.tarkamleague.models.responses.ClubResp(c.id, c.name) " +
            "FROM Club c WHERE c.name LIKE %?1%")
    Page<ClubResp> findAllRespByNameLike(String name, Pageable pageable);

    @Query(value = "SELECT " +
            "new com.fghifarr.tarkamleague.configs.planner.ClubPlanner(c.id, c.name) " +
            "FROM Club c")
    List<ClubPlanner> findAllClubPlanners();

    @Query(value = "SELECT " +
            "new com.fghifarr.tarkamleague.models.responses.ClubResp(c.id, c.name) " +
            "FROM Club c WHERE c.id = ?1")
    ClubResp findRespById(Long id);

    Club findByName(String name);
}
