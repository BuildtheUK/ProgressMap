package com.example.maphub.services;

import com.example.maphub.PasswordUtil;
import com.example.maphub.entities.User;
import com.example.maphub.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public void register(String uuid, String hashedPassword) {

        User user = new User();
        user.setUuid(uuid);
        user.setPassword(hashedPassword);
        user.setVerified(false);

        repo.save(user);
    }

    public User login(String uuid, String password) {
        return repo.findByUuid(uuid)
                .filter(user -> PasswordUtil.verify(password, user.getPassword()))
                .orElse(null);
    }

    public User findByUuid(String uuid){
        return  repo.findByUuid(uuid).orElse(null);
    }

    public boolean userExists(String uuid){
        return repo.findByUuid(uuid).isPresent();
    }

    public boolean userAccountIsActivated(String uuid) {return repo.findByUuid(uuid).filter(User::isVerified).isPresent();}

    public void verifyUser(String uuid) {
            repo.findByUuid(uuid).ifPresent(user -> {
                user.setVerified(true);
                repo.save(user);
            });
    }

    public void updatePassword(String uuid, String password){
        String hashed = PasswordUtil.hash(password);
        repo.updatePasswordByUuid(uuid,hashed);
    }

    public void deleteByUuid(String uuid){
        repo.deleteByUuid(uuid);
    }
}