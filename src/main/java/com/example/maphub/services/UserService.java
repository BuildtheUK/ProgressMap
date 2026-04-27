package com.example.maphub.services;

import com.example.maphub.PasswordUtil;
import com.example.maphub.entities.User;
import com.example.maphub.entities.VerificationResult;
import com.example.maphub.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User register(VerificationResult r) {

        User user = new User();
        user.setUsername(r.username); //get uuid from proxy from username. if username doesn't exist fail
        user.setPassword(r.passwordHash);

        return repo.save(user);
    }

    public User login(String username, String password) {
        return repo.findByUsername(username) //fetch uuid from proxy by username
                .filter(user -> PasswordUtil.verify(password, user.getPassword()))
                .orElse(null);
    }

    public User findByUsername(String username){
        return  repo.findByUsername(username).orElse(null);
    }

    public boolean userExists(String username){
        return repo.findByUsername(username).isPresent();
    }
}