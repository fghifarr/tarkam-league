package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
