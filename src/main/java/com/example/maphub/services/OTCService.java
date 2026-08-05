package com.example.maphub.services;

import com.example.maphub.entities.otc.OneTimeCode;
import com.example.maphub.repositories.OneTimeCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Transactional
public class OTCService {
    private final OneTimeCodeRepository repo;
    private final ProxyAPIService proxyAPIService;

    public OTCService(OneTimeCodeRepository repo, ProxyAPIService proxyAPIService) {
        this.repo = repo;
        this.proxyAPIService = proxyAPIService;
    }

    public boolean isValidCode(String uuid, int code,String purpose){
            return repo.findByUuidAndCode(uuid, code)
                    .map(otc -> {
                        if (LocalDateTime.now().isBefore(otc.getExpiry()) && otc.getPurpose().equals(purpose)) {
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
        boolean success = proxyAPIService.sendOTC(uuid,c.getCode());
        if (!success){
            throw new RuntimeException("Unable to send OTC");
        }
        repo.save(c);
    }
}
