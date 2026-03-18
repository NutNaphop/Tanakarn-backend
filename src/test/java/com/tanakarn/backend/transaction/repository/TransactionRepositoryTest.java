package com.tanakarn.backend.transaction.repository;

import com.tanakarn.backend.account.entity.Account;
import com.tanakarn.backend.account.repository.AccountRepository;
import com.tanakarn.backend.transaction.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void shouldSaveTransaction() {
        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setBalance(1000);
        account.setUser(null);

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(500);
        transaction.setAccount(account);
        transactionRepository.save(transaction);

        assertNotNull(transactionRepository.findById(1L));
    }

    @Test
    void shouldFindByAccountIdOrderByTimestampDesc() {
        final LocalDateTime localDateTime = LocalDateTime.now();

        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setBalance(1000);
        account.setUser(null);

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(500);
        transaction.setAccount(account);
        transaction.setType("TRANSFER_IN");
        transaction.setTimestamp(localDateTime.minusSeconds(1));

        Transaction transaction2 = new Transaction();
        transaction2.setAmount(500);
        transaction2.setAccount(account);
        transaction2.setType("TRANSFER_OUT");
        transaction2.setTimestamp(localDateTime.plusSeconds(1));

        transactionRepository.save(transaction);
        transactionRepository.save(transaction2);

        List<Transaction> fetchTransaction = transactionRepository.findByAccountIdOrderByTimestampDesc(account.getId());

        assertNotNull(fetchTransaction);
        assertEquals(2, fetchTransaction.size());
        assertTrue(
                fetchTransaction.get(0).getTimestamp().isAfter(fetchTransaction.get(1).getTimestamp())
        );
    }
}
