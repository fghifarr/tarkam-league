package com.fghifarr.tarkamleague.configs.init;

import com.fghifarr.tarkamleague.configs.constants.RoleConstant;
import com.fghifarr.tarkamleague.configs.constants.RoleGroupConstant;
import com.fghifarr.tarkamleague.entities.Role;
import com.fghifarr.tarkamleague.entities.RoleGroup;
import com.fghifarr.tarkamleague.repositories.RoleGroupRepository;
import com.fghifarr.tarkamleague.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RoleGroupInitService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleGroupRepository roleGroupRepository;

    int init() {
        int counter = 0;

        Role administrator = roleRepository.findByName(RoleConstant.ADMINISTRATOR);
        Role creator = roleRepository.findByName(RoleConstant.CREATOR);
        Role editor = roleRepository.findByName(RoleConstant.EDITOR);
        Role viewer = roleRepository.findByName(RoleConstant.VIEWER);

        List<RoleGroup> roleGroupList = List.of(
                new RoleGroup(RoleGroupConstant.ADMIN, Set.of(administrator)),
                new RoleGroup(RoleGroupConstant.DATA_ENTRY, Set.of(creator, editor, viewer)),
                new RoleGroup(RoleGroupConstant.VISITOR, Set.of(viewer))
        );

        for (RoleGroup roleGroup : roleGroupList) {
            roleGroupRepository.saveAndFlush(roleGroup);
            counter++;
        }

        return counter;
    }
}
