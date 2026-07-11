package com.example.maphub.repositories;

import com.example.maphub.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUuid(String uuid);

    void deleteByUuid(String uuid);
}
