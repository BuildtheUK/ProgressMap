package com.example.maphub.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username; //will store an mc uuid in the future

    private String password;

    public void setUsername(String username) {
       this.username = username;
    }

    public void setPassword(String hashed) {
        this.password = hashed;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // getters/setters
}