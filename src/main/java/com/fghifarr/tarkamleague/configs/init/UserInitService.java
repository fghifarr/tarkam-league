package com.fghifarr.tarkamleague.configs.init;

import com.fghifarr.tarkamleague.configs.constants.RoleGroupConstant;
import com.fghifarr.tarkamleague.entities.RoleGroup;
import com.fghifarr.tarkamleague.entities.User;
import com.fghifarr.tarkamleague.repositories.RoleGroupRepository;
import com.fghifarr.tarkamleague.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserInitService {

    @Autowired
    private RoleGroupRepository roleGroupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;

    int init() {
        int counter = 0;

        RoleGroup admin = roleGroupRepository.findByName(RoleGroupConstant.ADMIN);
        RoleGroup dataEntry = roleGroupRepository.findByName(RoleGroupConstant.DATA_ENTRY);
        RoleGroup visitor = roleGroupRepository.findByName(RoleGroupConstant.VISITOR);

        List<User> userList = List.of(
                new User("admin1", encoder.encode("admin1Pass"), admin),
                new User("admin2", encoder.encode("admin2Pass"), admin),
                new User("dataEntry1", encoder.encode("dataEntry1Pass"), dataEntry),
                new User("dataEntry2", encoder.encode("dataEntry1Pass"), dataEntry),
                new User("visitor1", encoder.encode("visitor1Pass"), visitor),
                new User("visitor2", encoder.encode("visitor2Pass"), visitor)
        );

        for (User user : userList) {
            userRepository.saveAndFlush(user);
            counter++;
        }

        return counter;
    }
}
