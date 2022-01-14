package com.fghifarr.tarkamleague.configs.init;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.entities.Role;
import com.fghifarr.tarkamleague.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleInitService {

    @Autowired
    private RoleRepository roleRepository;

    int init() {
        int counter = 0;

        List<Role> roleList = List.of(
                new Role(RoleConstant.ADMINISTRATOR),
                new Role(RoleConstant.CREATOR),
                new Role(RoleConstant.EDITOR),
                new Role(RoleConstant.VIEWER)
        );

        for (Role role : roleList) {
            roleRepository.saveAndFlush(role);
            counter++;
        }

        return counter;
    }
}
