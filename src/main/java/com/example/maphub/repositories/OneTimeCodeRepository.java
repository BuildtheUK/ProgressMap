package com.example.maphub.repositories;

import com.example.maphub.entities.OneTimeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OneTimeCodeRepository extends JpaRepository<OneTimeCode, Long> {
    Optional<OneTimeCode> findByUsernameAndCode(String username, int code);

    void deleteByUsername(String username);
}
