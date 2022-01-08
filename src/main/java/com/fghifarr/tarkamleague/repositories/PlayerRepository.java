package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.Player;
import com.fghifarr.tarkamleague.models.responses.PlayerResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT " +
            "new com.fghifarr.tarkamleague.models.responses.PlayerResp(p.id, p.name, c.name) " +
            "FROM Player p LEFT JOIN p.club c " +
            "WHERE p.name LIKE %?1% AND (?2 IS NULL OR c.id = ?2)")
    Page<PlayerResp> findAllRespWithQueries(String player, Long club, Pageable pageable);

    @Query("SELECT " +
            "new com.fghifarr.tarkamleague.models.responses.PlayerResp(p.id, p.name, c.name) " +
            "FROM Player p LEFT JOIN p.club c " +
            "WHERE p.id = ?1")
    PlayerResp findRespById(Long id);
}
