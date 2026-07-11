package com.example.maphub.services;

import com.example.maphub.PasswordUtil;
import com.example.maphub.entities.OneTimeCode;
import com.example.maphub.entities.User;
import com.example.maphub.entities.VerificationResult;
import com.example.maphub.repositories.OneTimeCodeRepository;
import com.example.maphub.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class OTCService {
    private final OneTimeCodeRepository repo;

    public OTCService(OneTimeCodeRepository repo) {
        this.repo = repo;
    }

    public boolean isValidCode(String uuid, int code){
            return repo.findByUuidAndCode(uuid, code)
                    .map(otc -> {
                        if (LocalDateTime.now().isBefore(otc.getExpiry())) {
                            repo.delete(otc);
                            return true;
                        }
                        return false;
                    })
                    .orElse(false);
    }


    //Creates a one timeCode, Stores it in the DB and sends to the players MC account.
    @Transactional
    public void createOneTimeCode(String uuid, String purpose)
    {
        repo.deleteByUuid(uuid);
        OneTimeCode c = new OneTimeCode();
        c.setUuid(uuid);
        c.setPurpose(purpose);
        c.setExpiry(LocalDateTime.now().plusMinutes(15));
        c.setCode(new SecureRandom().nextInt(100000,999999));
        System.out.println(c.getCode());
        repo.save(c);
    }
}
