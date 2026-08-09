package com.example.maphub.entities.otc;

public class VerificationResult {
    public String username;
    public String passwordHash;
    public VerificationResult(String username, String pHash) {
        this.username = username;
        this.passwordHash = pHash;
    }
}
