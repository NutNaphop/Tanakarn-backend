package com.tanakarn.backend.account.service;

import com.tanakarn.backend.account.entity.Account;
import com.tanakarn.backend.account.repository.AccountRepository;
import com.tanakarn.backend.transaction.entity.Transaction;
import com.tanakarn.backend.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository){
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transferMoney(Long fromId, Long toId, double amount) {
        // 1. ดึงข้อมูลบัญชีผู้โอนและผู้รับ
        Account fromAccount = accountRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("ไม่พบบัญชีผู้โอน"));
        Account toAccount = accountRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("ไม่พบบัญชีผู้รับ"));

        if(amount <= 0){
            throw new RuntimeException("จำนวนเงินต้องมากกว่า 0");
        }

        if(fromAccount.getBalance() < amount){
            throw new RuntimeException("ยอดเงินไม่พอโอน");
        }

        Transaction outTx = new Transaction(amount, "OUT");
        outTx.setAmount(amount);
        outTx.setType("TRANSFER_OUT");
        outTx.setTimestamp(LocalDateTime.now());
        outTx.setAccount(fromAccount); // เชื่อมหาคนโอน
        transactionRepository.save(outTx);

        Transaction inTx = new Transaction();
        inTx.setAmount(amount); // เก็บ 200 (เลขบวก)
        inTx.setType("TRANSFER_IN");
        inTx.setTimestamp(LocalDateTime.now());
        inTx.setAccount(toAccount); // เชื่อมหาคนรับ
        transactionRepository.save(inTx);

        // 2. เช็คยอดเงินว่าพอโอนไหม
        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("ยอดเงินในบัญชีไม่เพียงพอ");
        }

        // 3. ปรับยอดเงินใน Object (Logic การคำนวณ)
        double fromAccountBalance = fromAccount.getBalance();
        double toAccountBalance = toAccount.getBalance();
        fromAccount.setBalance(fromAccountBalance - amount);
        toAccount.setBalance(toAccountBalance + amount);

        // 4. บันทึกลงฐานข้อมูล
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}
