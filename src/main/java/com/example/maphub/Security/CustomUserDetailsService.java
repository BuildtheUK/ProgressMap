package com.example.maphub.Security;

import com.example.maphub.entities.User;
import com.example.maphub.services.UserService;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        User user = userService.findByUuid(uuid); // <- you need this method

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUuid(),
                user.getPassword(), // MUST be hashed
                Collections.emptyList() // roles (can add later)
        );
    }
}
