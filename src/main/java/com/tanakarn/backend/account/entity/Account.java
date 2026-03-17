package com.tanakarn.backend.account.entity;


import com.tanakarn.backend.auth.entity.User;
import jakarta.persistence.*;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String accountNumber;
    private double balance;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    public Account() {

    }

    public Account(String accountNumber, double balance){
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber(){ return accountNumber;}
    public double getBalance(){ return balance;}
    public User getUser(){ return user;}
    public long getId(){ return id;}

    public void setUser(User user){ this.user = user;}
    public void setBalance(double balance){ this.balance = balance;}
    public void setAccountNumber(String accountNumber){ this.accountNumber = accountNumber;}
    public void setId(long id){ this.id = id;}
}

