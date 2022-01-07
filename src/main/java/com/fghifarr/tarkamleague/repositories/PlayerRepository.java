package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
