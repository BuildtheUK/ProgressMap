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
public class OTCService {
    private final OneTimeCodeRepository repo;

    public OTCService(OneTimeCodeRepository repo) {
        this.repo = repo;
    }

    public VerificationResult fetchUserDetails(String username, int code){
        OneTimeCode otc = repo.findByUsernameAndCode(username,code).orElseThrow(() -> new RuntimeException("Invalid code"));
        if(LocalDateTime.now().isBefore( otc.getExpiry())) {
            repo.delete(otc);
            return new VerificationResult(
                    otc.getUsername(),
                    otc.getpHash()
            );
        }
        else{
            throw new RuntimeException("Invalid code used");
        }


    }


    @Transactional
    public void createOneTimeCode(String username, String password)
    {
        repo.deleteByUsername(username);
        String hashed = PasswordUtil.hash(password);
        OneTimeCode c = new OneTimeCode();
        c.setUsername(username);
        c.setpHash(hashed);
        c.setExpiry(LocalDateTime.now().plusMinutes(15));
        c.setCode(new SecureRandom().nextInt(999999));
        System.out.println(c.getCode());
        repo.save(c);
    }
}
