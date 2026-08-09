package com.example.maphub.repositories;

import com.example.maphub.entities.otc.OneTimeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OneTimeCodeRepository extends JpaRepository<OneTimeCode, Long> {
    Optional<OneTimeCode> findByUuidAndCode(String uuid, int code);

    void deleteByUuid(String uuid);
}
