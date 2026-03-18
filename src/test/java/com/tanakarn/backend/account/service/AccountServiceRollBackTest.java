package com.tanakarn.backend.account.service;

import com.tanakarn.backend.account.entity.Account;
import com.tanakarn.backend.account.repository.AccountRepository;
import com.tanakarn.backend.transaction.entity.Transaction;
import com.tanakarn.backend.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
public class AccountServiceRollBackTest {
    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @MockitoBean
    TransactionRepository transactionRepository;

    @Test
    void shouldRollbackWhenTransactionSavingFails() {
        // Arrange
        Account fromAccount = new Account();
        fromAccount.setBalance(1000);
        fromAccount.setAccountNumber("ACC01");
        accountRepository.save(fromAccount);

        Account toAccount = new Account();
        toAccount.setBalance(500);
        toAccount.setAccountNumber("ACC02");
        accountRepository.save(toAccount);

        doThrow(new RuntimeException("Transaction save failed"))
                .when(transactionRepository).save(any());

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                accountService.transferMoney(fromAccount.getId(), toAccount.getId(), 100)
        );

        // Assert
        assertEquals("Transaction save failed", exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage());

        Account reloadedFromAccount = accountRepository.findById(fromAccount.getId()).orElseThrow();
        Account reloadedToAccount = accountRepository.findById(toAccount.getId()).orElseThrow();

        assertEquals(1000, reloadedFromAccount.getBalance());
        assertEquals(500, reloadedToAccount.getBalance());

    }
}
