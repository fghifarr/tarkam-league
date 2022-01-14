package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.entities.Season;
import com.fghifarr.tarkamleague.models.responses.SeasonResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SeasonRepository extends JpaRepository<Season, Long> {

    @Query(value = "SELECT " +
            "new com.fghifarr.tarkamleague.models.responses.SeasonResp(s.id, s.name) " +
            "FROM Season s WHERE s.name LIKE %?1%")
    Page<SeasonResp> findAllRespByNameLike(String name, Pageable pageable);

    @Query(value = "SELECT " +
            "new com.fghifarr.tarkamleague.models.responses.SeasonResp(s.id, s.name) " +
            "FROM Season s WHERE s.id = ?1")
    SeasonResp findRespById(Long id);

    Season findByName(String name);
}
