package com.example.maphub.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="onetimecode")
public class OneTimeCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String pHash;
    private int code;
    private LocalDateTime expiry;


    public String getUsername() {
        return username;
    }

    public String getpHash() {
        return pHash;
    }

    public int getCode() {
        return code;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public void setCode(int code){
        this.code = code;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public void setExpiry(LocalDateTime t)
    {
        this.expiry = t;
    }
    public void setpHash (String hash)
    {
        this.pHash = hash;
    }


}
