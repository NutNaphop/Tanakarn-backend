package com.tanakarn.backend.transfer.dto.request;

public class TransferRequest {
    private Long fromAccountId;
    private Long toAccountId;
    private double amount;

    public Long getFromAccountId(){ return fromAccountId;}
    public Long getToAccountId(){ return toAccountId;}
    public double getAmount(){ return amount;}
    public void setFromAccountId(Long fromAccountId){ this.fromAccountId = fromAccountId;}
    public void setToAccountId(Long toAccountId){ this.toAccountId = toAccountId;}
    public void setAmount(double amount){ this.amount = amount;}
}
