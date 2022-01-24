package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.Club;
import com.fghifarr.tarkamleague.entities.MatchClub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchClubRepository extends JpaRepository<MatchClub, Long> {

    List<MatchClub> findAllByClub(Club club);

}
