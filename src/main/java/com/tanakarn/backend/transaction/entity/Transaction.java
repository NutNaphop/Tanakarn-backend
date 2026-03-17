package com.tanakarn.backend.transaction.entity;

import com.tanakarn.backend.account.entity.Account;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private  double amount;
    private LocalDateTime timestamp;
    private String type;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Transaction(){}
    public Transaction(double amount, String type){
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public long getId(){ return id;}
    public double getAmount(){ return amount;}
    public LocalDateTime getTimestamp(){ return timestamp;}
    public String getType(){ return type;}
    public Account getAccount(){ return account;}

    public void setId(long id){ this.id = id;}
    public void setAmount(double amount){ this.amount = amount;}
    public void setTimestamp(LocalDateTime timestamp){ this.timestamp = timestamp;}
    public void setType(String type){ this.type = type;}
    public void setAccount(Account account){ this.account = account;}

}
