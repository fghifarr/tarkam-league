package com.fghifarr.tarkamleague.configs.security;

import com.fghifarr.tarkamleague.entities.User;
import com.fghifarr.tarkamleague.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserPrincipalService implements UserDetailsService {

    UserRepository userRepository;

    UserPrincipalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("Can't find user with username: " + username);

        return new UserPrincipal(user);
    }
}
