package com.example.maphub.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    private String password;

    private boolean verified;

    public void setVerified(boolean verified)
    {
        this.verified = verified;
    }
    public boolean isVerified()
    {
        return verified;
    }

    public void setUuid(String uuid) {
       this.uuid = uuid;
    }

    public void setPassword(String hashed) {
        this.password = hashed;
    }

    public long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPassword() {
        return password;
    }


    // getters/setters
}