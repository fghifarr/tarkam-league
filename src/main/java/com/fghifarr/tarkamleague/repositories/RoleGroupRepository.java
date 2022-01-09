package com.fghifarr.tarkamleague.repositories;

import com.fghifarr.tarkamleague.entities.RoleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleGroupRepository extends JpaRepository<RoleGroup, Long> {
    RoleGroup findByName(String name);
}
