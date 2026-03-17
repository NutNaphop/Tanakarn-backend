package com.tanakarn.backend.account.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateAccountRequest {
    @NotBlank(message = "ชื่อลูกค้าห้ามว่าง")
    private String name;

    @Min(value = 0, message = "ยอดเงินต้องไม่ต่ำกว่า 0")
    private double initialBalance;

    public String getName(){ return name;}
    public double getInitialBalance(){return initialBalance;}

    public void setName(String name){this.name = name;}
    public void setInitialBalance(double initialBalance){this.initialBalance = initialBalance;}
}
