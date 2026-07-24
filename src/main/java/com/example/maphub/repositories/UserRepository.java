package com.example.maphub.repositories;

import com.example.maphub.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUuid(String uuid);

    void deleteByUuid(String uuid);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.uuid = :uuid")
    void updatePasswordByUuid(@Param("uuid") String uuid, @Param("password") String password);


}
