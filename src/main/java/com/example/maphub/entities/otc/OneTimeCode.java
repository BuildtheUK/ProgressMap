package com.example.maphub.entities.otc;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="onetimecode")
public class OneTimeCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int code;
    private LocalDateTime expiry;
    private String uuid;
    private String purpose;


    public int getCode() {
        return code;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }
    public String getUuid(){
        return uuid;
    }
    public String getPurpose(){
        return purpose;
    }

    public void setCode(int code){
        this.code = code;
    }
    public void setExpiry(LocalDateTime t)
    {
        this.expiry = t;
    }
    public void setUuid(String uuid){
        this.uuid = uuid;
    }
    public void setPurpose(String purpose){
        this.purpose = purpose;
    }


}
