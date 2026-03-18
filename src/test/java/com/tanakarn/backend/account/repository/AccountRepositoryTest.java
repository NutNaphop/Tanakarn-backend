package com.tanakarn.backend.account.repository;

import com.tanakarn.backend.account.entity.Account;
import com.tanakarn.backend.auth.entity.User;
import com.tanakarn.backend.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindAccountByUserId(){
        User user = new User();
        user.setUsername("Naphop");
        user.setPassword("");

        userRepository.save(user);

        Account newAccount = new Account();
        newAccount.setBalance(1000);
        newAccount.setAccountNumber("ACC001");
        newAccount.setUser(user);

        accountRepository.save(newAccount);

        Account account = accountRepository.findAccountById(user.getId());
        assertNotNull(account);

        assertEquals(account.getUser().getUsername(), newAccount.getUser().getUsername());
        assertEquals(account.getAccountNumber(), newAccount.getAccountNumber());
    }
}
